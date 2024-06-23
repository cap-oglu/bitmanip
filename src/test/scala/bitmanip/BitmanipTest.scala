package bitmanip

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class BitManipSpec extends AnyFreeSpec  with Matchers {
    /*"Bit Manipulation Unit should calculate" in {
        simulate(new Bitmanip()) { dut =>

            dut.io.cmd.valid.poke(true.B)

            dut.io.cmd.bits.inst.funct.poke(5.U)
            dut.io.cmd.bits.rs1.poke(1.U)
            dut.io.cmd.bits.rs2.poke(1.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xd.poke(false.B)
            dut.clock.step()
            dut.clock.step()
        
        
        }
    }*/

    "Bit Manipulation Unit version 2 should pass the tests" in {
        simulate(new BitmanipV2()) { dut =>

            dut.io.cmd.valid.poke(true.B)

            //clmul
            dut.io.cmd.bits.inst.opcode.poke(51.U)
            dut.io.cmd.bits.inst.funct.poke(5.U)
            dut.io.cmd.bits.rs1.poke(1.U)
            dut.io.cmd.bits.rs2.poke(3.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.io.cmd.bits.inst.xd.poke(false.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            //dut.io.resp.bits.data.expect(1.U)
            dut.clock.step()

            /*
            //bext
            dut.io.cmd.bits.inst.opcode.poke(51.U)
            dut.io.cmd.bits.inst.funct.poke(36.U)
            dut.io.cmd.bits.inst.xd.poke(true.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke(5.U)
            dut.io.cmd.bits.rs2.poke(2.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //bclr
            dut.io.cmd.bits.inst.opcode.poke(51.U)
            dut.io.cmd.bits.inst.funct.poke(36.U)
            dut.io.cmd.bits.inst.xd.poke(false.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke(5.U)
            dut.io.cmd.bits.rs2.poke(0.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()
            
            //bclri
            dut.io.cmd.bits.inst.opcode.poke(19.U)
            dut.io.cmd.bits.inst.funct.poke(36.U)
            dut.io.cmd.bits.inst.xd.poke(false.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke(5.U)
            dut.io.cmd.bits.inst.rs2.poke(0.U)
            //dut.io.cmd.bits.rs2.poke(0.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //bexti
            dut.io.cmd.bits.inst.opcode.poke(19.U)
            dut.io.cmd.bits.inst.funct.poke(36.U)
            dut.io.cmd.bits.inst.xd.poke(true.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke(5.U)
            dut.io.cmd.bits.inst.rs2.poke(2.U)
            //dut.io.cmd.bits.rs2.poke(0.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //binv
            dut.io.cmd.bits.inst.opcode.poke(51.U)
            dut.io.cmd.bits.inst.funct.poke(52.U)
            dut.io.cmd.bits.inst.xd.poke(false.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke(5.U)
            //dut.io.cmd.bits.inst.rs2.poke(2.U)
            dut.io.cmd.bits.rs2.poke(2.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //binvi
            dut.io.cmd.bits.inst.opcode.poke(19.U)
            dut.io.cmd.bits.inst.funct.poke(52.U)
            dut.io.cmd.bits.inst.xd.poke(false.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke(5.U)
            dut.io.cmd.bits.inst.rs2.poke(1.U)
            //dut.io.cmd.bits.rs2.poke(2.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()
            
            //bset
            dut.io.cmd.bits.inst.opcode.poke(51.U)
            dut.io.cmd.bits.inst.funct.poke(20.U)
            dut.io.cmd.bits.inst.xd.poke(false.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke(1.U)
            //dut.io.cmd.bits.inst.rs2.poke(1.U)
            dut.io.cmd.bits.rs2.poke(2.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //bseti
            dut.io.cmd.bits.inst.opcode.poke(19.U)
            dut.io.cmd.bits.inst.funct.poke(20.U)
            dut.io.cmd.bits.inst.xd.poke(false.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke(1.U)
            dut.io.cmd.bits.inst.rs2.poke(2.U)
            //dut.io.cmd.bits.rs2.poke(2.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //sh1add 
            dut.io.cmd.bits.inst.opcode.poke(51.U) 
            dut.io.cmd.bits.inst.funct.poke(16.U)
            dut.io.cmd.bits.inst.xd.poke(false.B)
            dut.io.cmd.bits.inst.xs1.poke(true.B)
            dut.io.cmd.bits.inst.xs2.poke(false.B)
            dut.io.cmd.bits.rs1.poke(4.U)
            dut.io.cmd.bits.rs2.poke(1.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //sh2add ve sh3add'i test etmeye gerek yok
            
            */
            /*
            //andn
            dut.io.cmd.bits.inst.opcode.poke(51.U)
            dut.io.cmd.bits.inst.funct.poke(32.U)
            dut.io.cmd.bits.inst.xd.poke(true.B)
            dut.io.cmd.bits.inst.xs1.poke(true.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke(1.U)
            dut.io.cmd.bits.rs2.poke(2.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //xnor
            dut.io.cmd.bits.inst.opcode.poke(51.U)
            dut.io.cmd.bits.inst.funct.poke(32.U)
            dut.io.cmd.bits.inst.xd.poke(true.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(false.B)
            dut.io.cmd.bits.rs1.poke(0.U)
            dut.io.cmd.bits.rs2.poke(0.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //sext.b
            dut.io.cmd.bits.inst.opcode.poke(19.U)
            dut.io.cmd.bits.inst.funct.poke(48.U)
            dut.io.cmd.bits.inst.xd.poke(false.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke(128.U)
            dut.io.cmd.bits.inst.rs2.poke(4.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //sext.h
            dut.io.cmd.bits.inst.opcode.poke(19.U)
            dut.io.cmd.bits.inst.funct.poke(48.U)
            dut.io.cmd.bits.inst.xd.poke(false.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke(128.U)
            dut.io.cmd.bits.inst.rs2.poke(5.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //zext.h
            dut.io.cmd.bits.inst.opcode.poke(51.U)
            dut.io.cmd.bits.inst.funct.poke(4.U)
            dut.io.cmd.bits.inst.xd.poke(true.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(false.B)
            dut.io.cmd.bits.rs1.poke(128.U)
            dut.io.cmd.bits.inst.rs2.poke(0.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //rol
            dut.io.cmd.bits.inst.opcode.poke(51.U)
            dut.io.cmd.bits.inst.funct.poke(48.U)
            dut.io.cmd.bits.inst.xd.poke(false.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke("b1111000000000000_0000000000000000".U)
            dut.io.cmd.bits.rs2.poke(4.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()
            
            //ror
            dut.io.cmd.bits.inst.opcode.poke(51.U)
            dut.io.cmd.bits.inst.funct.poke(48.U)
            dut.io.cmd.bits.inst.xd.poke(true.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke("b0000000000000000_0000000000001111".U)
            dut.io.cmd.bits.rs2.poke(4.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //rori
            dut.io.cmd.bits.inst.opcode.poke(19.U)
            dut.io.cmd.bits.inst.funct.poke(48.U)
            dut.io.cmd.bits.inst.xd.poke(true.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke("b0000000000000000_0000000000001111".U)
            dut.io.cmd.bits.inst.rs2.poke(4.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //rev8
            dut.io.cmd.bits.inst.opcode.poke(19.U)
            dut.io.cmd.bits.inst.funct.poke(52.U)
            dut.io.cmd.bits.inst.xd.poke(true.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke("b0000000000001111_0000000000001111".U)
            dut.io.cmd.bits.inst.rs2.poke(24.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //orn
            dut.io.cmd.bits.inst.opcode.poke(51.U)
            dut.io.cmd.bits.inst.funct.poke(32.U)
            dut.io.cmd.bits.inst.xd.poke(true.B)
            dut.io.cmd.bits.inst.xs1.poke(true.B)
            dut.io.cmd.bits.inst.xs2.poke(false.B)
            dut.io.cmd.bits.rs1.poke(0.U)
            dut.io.cmd.bits.rs2.poke(1.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //orc.b
            dut.io.cmd.bits.inst.opcode.poke(19.U)
            dut.io.cmd.bits.inst.funct.poke(20.U)
            dut.io.cmd.bits.inst.xd.poke(true.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke("b0000000000001111_0000000000001111".U)
            dut.io.cmd.bits.inst.rs2.poke(7.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //clz
            dut.io.cmd.bits.inst.opcode.poke(19.U)
            dut.io.cmd.bits.inst.funct.poke(48.U)
            dut.io.cmd.bits.inst.xd.poke(false.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke("b0000000000000000_0000000000000001".U)
            dut.io.cmd.bits.inst.rs2.poke(0.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //cpop
            dut.io.cmd.bits.inst.opcode.poke(19.U)
            dut.io.cmd.bits.inst.funct.poke(48.U)
            dut.io.cmd.bits.inst.xd.poke(false.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke("b0000000000000000_0000000000000011".U)
            dut.io.cmd.bits.inst.rs2.poke(2.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            //ctz
            dut.io.cmd.bits.inst.opcode.poke(19.U)
            dut.io.cmd.bits.inst.funct.poke(48.U)
            dut.io.cmd.bits.inst.xd.poke(false.B)
            dut.io.cmd.bits.inst.xs1.poke(false.B)
            dut.io.cmd.bits.inst.xs2.poke(true.B)
            dut.io.cmd.bits.rs1.poke("b0000000000000001_0000000000000000".U)
            dut.io.cmd.bits.inst.rs2.poke(1.U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()
            */

            //max
            dut.io.cmd.bits.inst.opcode.poke(51.U)
            dut.io.cmd.bits.inst.funct.poke(5.U)
            dut.io.cmd.bits.inst.xd.poke(true.B)
            dut.io.cmd.bits.inst.xs1.poke(true.B)
            dut.io.cmd.bits.inst.xs2.poke(false.B)
            dut.io.cmd.bits.rs1.poke("b1111111111111111_1111111111111000".U)
            dut.io.cmd.bits.rs2.poke("b1111111111111111_1111111111111110".U)
            dut.io.cmd.bits.inst.rd.poke(0.U)
            dut.clock.step()

            

        }
   
    }
}