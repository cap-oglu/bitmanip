package queue

import chisel3._
import chisel3.util._


class QueueEx extends Module {
  val io = IO(new Bundle {
    //val ready = Input(Bool())
    val in = Flipped(Decoupled(UInt(8.W)))
    val out = Decoupled(UInt(8.W))
  })

  val queue = Queue(io.in, 16)
  queue.ready := io.out.ready
  io.out.valid := queue.valid
  io.out.bits := queue.bits

  //printf(cf"Queue :  ${queue}")


 
}