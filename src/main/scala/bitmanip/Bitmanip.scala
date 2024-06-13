package bitmanip

import chisel3._
import chisel3.util._


class RoCCInstruction extends Bundle {
  val funct = Bits(7.W)
  val rs2 = Bits(32.W)
  val rs1 = Bits(32.W)
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



