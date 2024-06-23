package bitmanip

import chisel3._
import chisel3.util._


class RoCCInstruction extends Bundle {
  val funct = Bits(7.W)
  val rs2 = Bits(5.W)
  val rs1 = Bits(5.W)
  val xd = Bool()
  val xs1 = Bool()
  val xs2 = Bool()
  val rd = Bits(5.W)
  val opcode = Bits(7.W)
}
class RoCCCommand extends Bundle{
  val inst = new RoCCInstruction
  val rs1 = Bits(32.W)
  val rs2 = Bits(32.W)
}

class RoCCResponse extends Bundle{
  val rd = Bits(5.W)
  val data = Bits(32.W)
}

class RoCCCoreIO(val nRoCCCSRs: Int = 0) extends Bundle{
  val cmd = Flipped(Decoupled(new RoCCCommand))
  val resp = Decoupled(new RoCCResponse)
  
  val busy = Output(Bool())
  val interrupt = Output(Bool())
  val exception = Input(Bool())
  
}

class Bitmanip extends Module {
  val io = IO(new RoCCCoreIO)

  val funct = io.cmd.bits.inst.funct 
  val in1 = io.cmd.bits.rs1 
  val in2 = io.cmd.bits.rs2 
  val out = Wire(UInt(32.W)) 

  val funct3_0 = io.cmd.bits.inst.xs2
  val funct3_1 = io.cmd.bits.inst.xs1
  val funct3_2 = io.cmd.bits.inst.xd

  val clmul_rs1 = Mux(funct === 5.U && funct3_0, in1, Reverse(in1))
  val clmul_rs2 = Mux(funct === 5.U && funct3_0, in2, Reverse(in2))
  val clmul = clmul_rs2.asBools.zipWithIndex.map({
    case (b, i) => Mux(b, clmul_rs1 << i, 0.U)
  }).reduce(_ ^ _)(31,0)
  val clmulr = Reverse(clmul)
  val clmulh = Cat(0.U(1.W), clmulr(32-1,1))



  
  when(funct === 32.U) { // ANDNOT operation
    out := in1 & (~in2)
  }.elsewhen(funct === 5.U && funct3_0 && !funct3_1 && !funct3_2) { //clmul
    out := clmul
  }.elsewhen(funct === 5.U && funct3_0 && funct3_1 && !funct3_2) { //clmulh
    out := clmulh
  }.elsewhen(funct === 5.U && !funct3_0 && funct3_1 && !funct3_2) { //clmulr
    out := clmulr
  }.otherwise { // Default case if none of the funct match
    out := 0.U
  }

    // Only execute the operation when a command is fired. 
    when(io.cmd.fire) { 
      // Check if the function matches the intended operation 
      when(funct === 32.U) { 
        // Writing the result to the destination register 
        io.resp.valid := true.B 
        io.resp.bits.rd := io.cmd.bits.inst.rd 
        io.resp.bits.data := out 
      }.elsewhen(funct === 5.U && funct3_0 && !funct3_1 && !funct3_2){
        io.resp.valid := true.B 
        io.resp.bits.rd := io.cmd.bits.inst.rd 
        io.resp.bits.data := out 
      }.elsewhen(funct === 5.U && !funct3_0 && funct3_1 && !funct3_2){
        io.resp.valid := true.B 
        io.resp.bits.rd := io.cmd.bits.inst.rd 
        io.resp.bits.data := out 
      }.elsewhen(funct === 5.U && funct3_0 && funct3_1 && !funct3_2){
        io.resp.valid := true.B 
        io.resp.bits.rd := io.cmd.bits.inst.rd 
        io.resp.bits.data := out 
      }

      
    } 
        io.resp.valid := false.B
        io.resp.bits.rd := io.cmd.bits.inst.rd 
        io.resp.bits.data := out

        // Setting command ready signal 
        io.cmd.ready := !io.resp.valid 
        // Ensuring the module is busy only when processing a command 
        io.busy := io.cmd.valid 
        // No interrupts or memory interface in use 
        io.interrupt := false.B 
        //io.mem.req.valid := false.B 
        printf("BitManipUnit: in1 = %x, in2 = %x, out = %x, funct = %x\n", in1, in2, out, funct) 

  
}


class BitmanipV2 extends Module {

  val io = IO(new RoCCCoreIO)

  val funct = io.cmd.bits.inst.funct 
  val in1 = io.cmd.bits.rs1 
  val in2 = io.cmd.bits.rs2 
  val out = Wire(UInt(32.W))
  val funct3 = Cat(io.cmd.bits.inst.xd, io.cmd.bits.inst.xs1, io.cmd.bits.inst.xs2)
  val opcode = io.cmd.bits.inst.opcode

  val clmul_rs1 = Mux(funct === 5.U && funct3 === 1.U, in1, Reverse(in1))
  val clmul_rs2 = Mux(funct === 5.U && funct3 === 1.U, in2, Reverse(in2))
  val clmul = clmul_rs2.asBools.zipWithIndex.map({
    case (b, i) => Mux(b, clmul_rs1 << i, 0.U)
  }).reduce(_ ^ _)(31,0)
  val clmulr = Reverse(clmul)
  val clmulh = Cat(0.U(1.W), clmulr(32-1,1))


  io.resp.valid := false.B
  io.resp.bits.rd := io.cmd.bits.inst.rd
  out := 0.U 
  io.resp.bits.data := out

  when(io.cmd.fire){
    when(funct === 32.U && funct3 === 7.U && opcode === 51.U){
      printf("ANDNOT\n")
      out := in1 & (~in2)
      
    }.elsewhen(funct === 5.U && funct3 === 1.U && opcode === 51.U){
      printf("CLMUL\n")
      out := clmul
      
    }.elsewhen(funct === 5.U && funct3 === 3.U && opcode === 51.U){
      printf("CLMULH\n")
      out := clmulh

    }.elsewhen(funct === 5.U && funct3 === 2.U && opcode === 51.U){
      printf("CLMULR\n")
      out := clmulr

    }.elsewhen(Cat(funct, io.cmd.bits.inst.rs2) === 1536.U && funct3 === 1.U && opcode === 19.U){
      printf("CLZ\n")
      out := clz(in1)

    }.elsewhen(Cat(funct, io.cmd.bits.inst.rs2) === 1538.U && funct3 === 1.U && opcode === 19.U){
      printf("CPOP\n")
      out := cpop(in1)

    }.elsewhen(Cat(funct, io.cmd.bits.inst.rs2) === 1537.U && funct3 === 1.U && opcode === 19.U){
      printf("CTZ\n")
      out := ctz(in1)

    }.elsewhen(funct === 5.U && funct3 === 6.U && opcode === 51.U){
      //turn this into signed
      printf("MAX\n")
      out := Mux(in1.asSInt >= in2.asSInt, in1, in2)

    }.elsewhen(funct === 5.U && funct3 === 7.U && opcode === 51.U){
      printf("MAXU\n")
      out := Mux(in1 >= in2, in1, in2)

    }.elsewhen(funct === 5.U && funct3 === 4.U && opcode === 51.U){
      //turn this into signed
      printf("MIN\n")
      out := Mux(in1.asSInt <= in2.asSInt, in1, in2)

    }.elsewhen(funct === 5.U && funct3 === 5.U && opcode === 51.U){
      printf("MINU\n")
      out := Mux(in1 <= in2, in1, in2)

    }.elsewhen(Cat(funct, io.cmd.bits.inst.rs2) === 647.U && funct3 === 5.U && opcode === 19.U){
      printf("ORCB\n")
      out := orcb(in1)

    }.elsewhen(funct === 32.U && funct3 === 6.U && opcode === 51.U){
      printf("ORN\n")
      out := in1 | (~in2)

    }.elsewhen(Cat(funct, io.cmd.bits.inst.rs2) === 1688.U && funct3 === 5.U && opcode === 19.U){
      //fonksiyon doğru çalışıyor ama rev8 tam olarak o fonksiyon mu, ona bakarsın?
      printf("REV8\n")
      out := rev8(in1)

    }.elsewhen(funct === 48.U && funct3 === 1.U && opcode === 51.U){
      printf("ROL\n")
      //bunlarda shiftleme kısmında hata olabilir
      out := rol(in1, in2, 32.U)

    }.elsewhen(funct === 48.U && funct3 === 5.U && opcode === 51.U){
      printf("ROR\n")
      out := ror(in1, in2, 32.U)

    }.elsewhen(funct === 48.U && funct3 === 5.U && opcode === 19.U){
      printf("RORI\n")
      out := rori(in1, io.cmd.bits.inst.rs2, 32.U)

    }.elsewhen((Cat(funct, io.cmd.bits.inst.rs2)) === 1540.U && funct3 === 1.U && opcode === 19.U){ //sext.b
      printf("SEXT.B\n")
      out := Cat(Fill(24, in1(7)), in1(7,0))

    }.elsewhen(Cat(funct, io.cmd.bits.inst.rs2) === 1541.U && funct3 === 1.U && opcode === 19.U){ //sext.h
      printf("SEXT.H\n")
      out := Cat(Fill(16, in1(15)), in1(15,0))

    }.elsewhen(Cat(funct, io.cmd.bits.inst.rs2) === 128.U && funct3 === 4.U && opcode === 51.U){ //zext.h
      printf("ZEXT.H\n")
      out := Cat(Fill(16, 0.U), in1(15,0))

    }.elsewhen(funct === 32.U && funct3 === 4.U && opcode === 51.U){ //xnor
      printf("XNOR\n")
      out := ~(in1 ^ in2)

    }.elsewhen(funct === 16.U && funct3 === 2.U && opcode === 51.U){ //sh1add
      printf("SH1ADD\n")
      out := in2 + (in1 << 1)

    }.elsewhen(funct === 16.U && funct3 === 4.U && opcode === 51.U){ //sh2add
      printf("SH2ADD\n")
      out := in2 + (in1 << 2)

    }.elsewhen(funct === 16.U && funct3 === 6.U && opcode === 51.U){ //sh3add
      printf("SH3ADD\n")
      out := in2 + (in1 << 3)

    }.elsewhen(funct === 20.U && funct3 === 1.U && opcode === 51.U){ //bset
      printf("BSET\n")
      out := in1 | (1.U(32.W) << in2(4,0))

    }.elsewhen(funct === 20.U && funct3 === 1.U && opcode === 19.U){ //bseti
      printf("BSETI\n")
      out := in1 | (1.U(32.W) << io.cmd.bits.inst.rs2)

    }.elsewhen(funct === 36.U && funct3 === 1.U && opcode === 51.U){ //bclr
      printf("BCLR\n")
      out := in1 & ~(1.U(32.W) << in2(4,0))

    }.elsewhen(funct === 36.U && funct3 === 1.U && opcode === 19.U){ //bclri
      printf("BCLRI\n")
      out := in1 & ~(1.U(32.W) << io.cmd.bits.inst.rs2)

    }.elsewhen(funct === 36.U && funct3 === 5.U && opcode === 51.U){ //bext
      printf("BEXT\n")
      out := (in1 >> in2(4,0)) & 1.U(32.W)

    }.elsewhen(funct === 36.U && funct3 === 5.U && opcode === 19.U){ //bexti
      printf("BEXTI\n")
      out := (in1 >> io.cmd.bits.inst.rs2) & 1.U(32.W)

    }.elsewhen(funct === 52.U && funct3 === 1.U && opcode === 51.U){ //binv
      printf("BINV\n")
      out := in1 ^ (1.U(32.W) << in2(4,0))

    }.elsewhen(funct === 52.U && funct3 === 1.U && opcode === 19.U){ //binvi
      printf("BINVI\n")
      out := in1 ^ (1.U(32.W) << io.cmd.bits.inst.rs2)

    }
    io.resp.valid := true.B
    io.resp.bits.rd := io.cmd.bits.inst.rd
    io.resp.bits.data := out

  }
  //normal version
  
  // Tek cycle olduğu için command ready her zaman true olacak 
  io.cmd.ready := true.B
  // Ensuring the module is busy only when processing a command 
  io.busy := io.cmd.fire
  // No interrupts or memory interface in use 
  io.interrupt := false.B 






  def highestSetBit(x: UInt): UInt = {
    val reversed = Reverse(x)  // Reverse the bits to find the highest from LSB side
    PriorityEncoder(reversed)  // Returns the index of the first 1 from LSB
  }

  def clz(x: UInt): UInt = {
    val index = highestSetBit(x)
    val clzResult = 32.U - index - 1.U
    clzResult
  }

  def cpop(rs: UInt): UInt = {
    val bits = VecInit(Seq.tabulate(32)(i => rs(i)))
    //https://stackoverflow.com/questions/68999352/are-vec-can-be-reduced-in-chisel
    val bitCount = bits.map(x => x.asUInt).reduce((x,y) => x +& y) 
    bitCount
  }

  def lowestSetBit(x: UInt): UInt = {
    PriorityEncoder(x)
  }

  // Function to simulate the RISC-V operation
  def ctz(rs: UInt): UInt = {
    val index = lowestSetBit(rs)
    index
  }

  def orcb(input: UInt): UInt = {
    var output = 0.U(32.W)
    val xlen = 32  // Assuming we are working with a 32-bit integer

    for (i <- 0 until xlen by 8) {
      // Extract the byte by shifting and masking
      val byte = (input >> i) & (0x000000ff).U
      val bit = byte.orR
      output |= (Fill(8,bit) << i)
    }

    output
  }

  def rev8(input: UInt): UInt = {
    var output = 0.U
    val xlen = 32  // Assuming a 32-bit integer

    // We loop over the byte positions in the input
    for (i <- 0 until xlen by 8) {
      val byteShift = xlen - 8 - i
      val byte = (input >> byteShift) & (0xff).U  // Extract each byte from the input
      output |= byte << i                    // Place the extracted byte into the reversed position in the output
    }

    output
  }

  def rol(rs1: UInt, rs2: UInt, xlen: UInt): UInt = {
    val shamtMask =  (0x1F).U                      // Mask for the shift amount bits
    val shamt = (rs2 & shamtMask)                    // Extract shift amount using the appropriate mask

    // Rotate left operation
    val result = (rs1 << shamt(4,0)) | (rs1 >> (xlen - shamt(4,0)))
    result
  }
  def ror(rs1: UInt, rs2: UInt, xlen: UInt): UInt = {
    val shamtMask =  (0x1F).U                      // Mask for the shift amount bits
    val shamt = rs2 & shamtMask                    // Extract shift amount using the appropriate mask

    // Rotate left operation
    val result = (rs1 >> shamt(4,0)) | (rs1 << (xlen - shamt(4,0)))
    result
  }
  def rori(rs1: UInt, rs2: UInt, xlen: UInt): UInt = {
    val shamtMask =  (0x1F).U                      // Mask for the shift amount bits
    val shamt = rs2 & shamtMask                    // Extract shift amount using the appropriate mask

    // Rotate left operation
    val result = (rs1 >> shamt(4,0)) | (rs1 << (xlen - shamt(4,0)))
    result
  }

  

  printf("in1 = [%b], in2 = [%b], out = [%b], funct = [%b], inst.rs2 = [%b], Cat = [%b]\n", 
  in1, in2, out, funct, io.cmd.bits.inst.rs2, (Cat(funct, io.cmd.bits.inst.rs2)))  
}

class Main extends App {
  /*val rs1_val: UInt32 = X(rs1)
  val rs2_val: UInt32 = X(rs2)
  var output: UInt32 = 0

  for (i <- 0 until xlen) {
    output = if (((rs2_val >> i) & 1) != 0) {
      output ^ (rs1_val << i)
    } else {
      output
    }
  }*/
}



