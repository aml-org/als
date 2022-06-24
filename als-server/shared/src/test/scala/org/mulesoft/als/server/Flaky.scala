package org.mulesoft.als.server

import org.mulesoft.als.logger.{Logger, MessageSeverity}
import org.scalatest._

import scala.concurrent.ExecutionContext.Implicits.global

/** Tag flaky tests in order to ignore failed outcomes
  */
object Flaky extends Tag("org.mulesoft.als.server.Flaky") {
  def isFlaky(testData: TestData): Boolean = testData.tags.contains("org.mulesoft.als.server.Flaky")
  def flakyFixture[T <: TestData](test: T)(superFixture: T => FutureOutcome)(implicit logger: Logger): FutureOutcome =
    if (isFlaky(test))
      superFixture(test)
        .change {
          case failed: Failed =>
            logger.log("isFlaky: failed", MessageSeverity.WARNING, test.name, "withFixture")
            failed.toSeq.foreach(e => logger.log(e.getMessage, MessageSeverity.DEBUG, test.name, "withFixture"))
            Canceled("Flaky test failed", failed.exception)
          case canceled: Canceled =>
            logger.log("isFlaky: Canceled", MessageSeverity.WARNING, test.name, "withFixture")
            canceled
          case Succeeded => Succeeded
          case Pending =>
            logger.log("isFlaky: Pending", MessageSeverity.DEBUG, test.name, "withFixture")
            Pending
        }
    else
      superFixture(test)
}
