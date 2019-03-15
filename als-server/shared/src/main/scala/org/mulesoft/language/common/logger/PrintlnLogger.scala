package org.mulesoft.language.common.logger

import org.mulesoft.language.common.logger.MessageSeverity.MessageSeverity

/**
  * Logger that prints to console.
  */
trait PrintLnLogger extends AbstractLogger {
  protected def executeLogging(msg: String, severity: MessageSeverity.Value): Unit = println(msg)
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
