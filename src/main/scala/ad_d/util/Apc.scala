package ad_d.util

import sys.process._
import net.liftweb.common.Logger
import java.io.IOException

class Apc(ethAddr: String) {
  private var powered_? = false
  val logger = Logger(classOf[Apc])

  private def exec_power_on = {
    powered_? = try {
      ("wakeonlan "+ethAddr !) == 0
    } catch {
      case ex: IOException => false
    }
    powered_?
  }

  def wakeUp = if (exec_power_on) true else {
    logger.warn(ethAddr + " could not be waked up!")
    false
  }
}
