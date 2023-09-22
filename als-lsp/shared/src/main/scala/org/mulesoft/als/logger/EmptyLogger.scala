package org.mulesoft.als.logger

/** Logger that logs nothing
  */
object EmptyLogger extends AbstractLogger {

  protected def executeLogging(msg: String, severity: MessageSeverity.Value): Unit = {
    // dummy
  }

  override protected val settings: Option[LoggerSettings] = None

  override def withSettings(settings: LoggerSettings): this.type = this
}
