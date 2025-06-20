package fifo

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class FifoSpec extends AnyFreeSpec with Matchers with ChiselSim {

  def resetDut (dut: Fifo) = {
    // inputs
    dut.io.wdata.poke(0.U(16.W))
    dut.io.pop.poke(false.B)
    dut.io.push.poke(false.B)

    // synchronous-reset
    dut.reset.poke(true.B)
    dut.clock.step(1)
    dut.reset.poke(false.B)
    dut.clock.step(1)
  }

  "FIFO should show empty when empty" in {
    simulate(new Fifo()) { dut =>
      resetDut(dut)

      dut.io.empty.expect(true.B)
      dut.io.rdata.expect(0.U(16.W))
    }
  }
}