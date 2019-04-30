package org.mulesoft.als.server.logger

import org.mulesoft.als.server.logger.MessageSeverity.MessageSeverity

/**
  * Logger that prints to console.
  */
object PrintLnLogger extends AbstractLogger {
  protected def executeLogging(msg: String, severity: MessageSeverity.Value): Unit = println(msg)

  override protected val settings: Option[LoggerSettings] = None

  override def withSettings(settings: LoggerSettings): PrintLnLogger.this.type = this
}

trait MutedLogger extends AbstractLogger {

  override protected val settings: Option[LoggerSettings] = None

  protected def executeLogging(msg: String, severity: MessageSeverity): Unit = {}

  /**
    * Sets logger configuration, both for the server and for the client.
    *
    * @param settings - logger settings object
    */
  override def withSettings(settings: LoggerSettings): this.type = this
}
