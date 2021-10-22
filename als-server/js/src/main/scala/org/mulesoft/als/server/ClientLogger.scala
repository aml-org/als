package org.mulesoft.als.server

import org.mulesoft.als.logger._
import org.mulesoft.als.vscode.{Logger => VsCodeLogger}

import scala.scalajs.js

@js.native
trait ClientLogger extends VsCodeLogger {}

case class ClientLoggerAdapter(clientLogger: ClientLogger) extends AbstractLogger {
  override protected def executeLogging(msg: String, severity: MessageSeverity.MessageSeverity): Unit =
    severity match {
      case MessageSeverity.ERROR   => clientLogger.error(msg)
      case MessageSeverity.WARNING => clientLogger.warn(msg)
      case MessageSeverity.DEBUG   => clientLogger.log(msg)
    }

  override protected val settings: Option[LoggerSettings] = None

  override def withSettings(settings: LoggerSettings): ClientLoggerAdapter.this.type = this

}
