package org.mulesoft.als.server

import org.mulesoft.als.logger.Logger
import org.mulesoft.als.logger.MessageSeverity.MessageSeverity
import org.scalatest.CompleteLastly.complete
import org.scalatest.{FutureOutcome, TestData}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

/** Tag flaky tests in order to ignore failed outcomes
  */
object FailedLogs {
  def loggerFixture[T <: TestData](
      test: T
  )(superFixture: T => FutureOutcome)(implicit logger: TestLogger): FutureOutcome = {
    logger.logList.clear()
    logger.logList.enqueue(s"Starting test: ${test.name}")
    complete {
      superFixture(test) // To be stackable, must call super.withFixture
    } lastly {
      logger.logList.clear()
    }
  }
}

case class TestLogger() extends Logger {

  val logList: mutable.Queue[String] = mutable.Queue[String]()

  /** Logs a message
    *
    * @param message
    *   \- message text
    * @param severity
    *   \- message severity
    * @param component
    *   \- component name
    * @param subComponent
    *   \- sub-component name
    */
  override def log(message: String, severity: MessageSeverity, component: String, subComponent: String): Unit =
    synchronized(logList += s"log\n\t$message\n\t$severity\n\t$component\n\t$subComponent")

  /** Logs a DEBUG severity message.
    *
    * @param message
    *   \- message text
    * @param component
    *   \- component name
    * @param subComponent
    *   \- sub-component name
    */
  override def debug(message: String, component: String, subComponent: String): Unit =
    synchronized(logList += s"debug\n\t$message\n\t$component\n\t$subComponent")

  /** Logs a WARNING severity message.
    *
    * @param message
    *   \- message text
    * @param component
    *   \- component name
    * @param subComponent
    *   \- sub-component name
    */
  override def warning(message: String, component: String, subComponent: String): Unit =
    synchronized(logList += s"warning\n\t$message\n\t$component\n\t$subComponent")

  /** Logs an ERROR severity message.
    *
    * @param message
    *   \- message text
    * @param component
    *   \- component name
    * @param subComponent
    *   \- sub-component name
    */
  override def error(message: String, component: String, subComponent: String): Unit =
    synchronized(logList += s"error\n\t$message\n\t$component\n\t$subComponent")
}
