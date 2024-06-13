package queue

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class QueueSpec extends AnyFreeSpec  with Matchers {
    "Queue should work" in {
        simulate(new QueueEx()) { dut =>
            dut.io.in.valid.poke(false.B)
            dut.io.in.bits.poke(1.U)
            dut.io.out.ready.poke(false.B)
            dut.clock.step()
            println(s"Out: ${dut.io.out.bits.peek()}, Queue: ${dut.queue.bits}")
            dut.io.in.valid.poke(true.B)
            dut.io.in.bits.poke(1.U)
            dut.io.out.ready.poke(false.B)
            dut.clock.step()
            println(s"Out: ${dut.io.out.bits.peek()}, Queue: ${dut.queue.bits}")
            dut.io.in.valid.poke(true.B)
            dut.io.in.bits.poke(2.U)
            dut.io.out.ready.poke(false.B)
            dut.clock.step()
            println(s"Out: ${dut.io.out.bits.peek()}, Queue: ${dut.queue.bits}")
            dut.io.in.valid.poke(true.B)
            dut.io.in.bits.poke(3.U)
            dut.io.out.ready.poke(false.B)
            dut.clock.step()
            println(s"Out: ${dut.io.out.bits.peek()}, Queue: ${dut.queue.bits}")
            dut.io.in.valid.poke(false.B)
            dut.io.in.bits.poke(3.U)
            dut.io.out.ready.poke(true.B)
            dut.clock.step()
            println(s"Out: ${dut.io.out.bits.peek()}, Queue: ${dut.queue.bits}")
            dut.io.in.valid.poke(false.B)
            dut.io.in.bits.poke(3.U)
            dut.io.out.ready.poke(true.B)
            dut.clock.step()
            println(s"Out: ${dut.io.out.bits.peek()}, Queue: ${dut.queue.bits}")
            dut.io.in.valid.poke(false.B)
            dut.io.in.bits.poke(3.U)
            dut.io.out.ready.poke(true.B)
            dut.clock.step()
            println(s"Out: ${dut.io.out.bits.peek()}, Queue: ${dut.queue.bits}")
        
        }
    }
}