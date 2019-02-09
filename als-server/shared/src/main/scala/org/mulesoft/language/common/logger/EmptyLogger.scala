// $COVERAGE-OFF$
package org.mulesoft.language.common.logger

/**
  * Logger that logs nothing
  */
class EmptyLogger extends AbstractLogger {

  protected def executeLogging(msg: String, severity: MessageSeverity.Value): Unit = {}

  override protected val settings: Option[LoggerSettings] = None

  override def withSettings(settings: LoggerSettings): this.type = this
}

// $COVERAGE-ON$
