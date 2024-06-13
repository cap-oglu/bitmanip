package bitmanip

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class BitManipSpec extends AnyFreeSpec  with Matchers {
    "Bit Manipulation Unit should calculate" in {
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
    }
}