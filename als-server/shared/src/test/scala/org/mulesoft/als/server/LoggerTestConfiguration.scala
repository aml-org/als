package org.mulesoft.als.server

import org.mulesoft.als.logger.Logger
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AsyncFunSuite

trait LoggerTestConfiguration extends AsyncFunSuite with BeforeAndAfterEach {

  val logger: TestLogger = TestLogger()

  override def beforeEach(): Unit = {
    super.beforeEach()
    Logger.withLogger(logger)
  }

  override def afterEach(): Unit = {
    super.afterEach()
    logger.cleanLogList()
  }
}
