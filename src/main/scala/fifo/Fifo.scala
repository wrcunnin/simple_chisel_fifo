package fifo

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage

class Fifo extends Module {
  val io = IO(new Bundle {
    val wdata = Input(UInt(16.W))
    val push = Input(Bool())
    val pop = Input(Bool())
    val rdata = Output(UInt(16.W))
    val empty = Output(Bool())
    val full = Output(Bool())
  })

  val buffer = Mem(16, UInt(16.W))
  val readPtr = Counter(16)
  val writePtr = Counter(16)
  val rdata = RegInit(0.U(16.W))
  val empty = RegInit(true.B)
  val full = RegInit(false.B)

  when (io.pop && !empty) {
    rdata := buffer(readPtr.value)
    readPtr.inc()
    full  := false.B
    empty := readPtr.value === writePtr.value
  } .elsewhen (io.push && !full) {
    buffer(writePtr.value) := io.wdata
    writePtr.inc()
    full  := readPtr.value === writePtr.value
    empty := false.B
  }

  io.rdata := rdata
  io.full  := full
  io.empty := empty
}

/**
 * Generate Verilog sources and save it in file Fifo.v
 */
object Fifo extends App {
  ChiselStage.emitSystemVerilogFile(
    new Fifo,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info", "-default-layer-specialization=enable")
  )
}