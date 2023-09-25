package org.mulesoft.als.logger

import org.mulesoft.als.logger.MessageSeverity.MessageSeverity

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/** Logger that prints to console.
  */
@JSExportAll
@JSExportTopLevel("PrintLnLogger")
object PrintLnLogger extends AbstractLogger {
  protected def executeLogging(msg: String, severity: MessageSeverity.Value): Unit =
    println(msg)

  override protected val settings: Option[LoggerSettings] = None

  override def withSettings(settings: LoggerSettings): PrintLnLogger.this.type =
    this
}

class MutedLogger extends AbstractLogger {

  override protected val settings: Option[LoggerSettings] = None

  protected def executeLogging(msg: String, severity: MessageSeverity): Unit = {
    // muted
  }

  /** Sets logger configuration, both for the server and for the client.
    *
    * @param settings
    *   \- logger settings object
    */
  override def withSettings(settings: LoggerSettings): this.type = this
}
