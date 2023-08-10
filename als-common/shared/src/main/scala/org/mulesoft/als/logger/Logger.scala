package org.mulesoft.als.logger

import org.mulesoft.als.logger.MessageSeverity.MessageSeverity
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait Logger {

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
  def log(message: String, severity: MessageSeverity, component: String, subComponent: String): Unit

  /** Logs a DEBUG severity message.
    *
    * @param message
    *   \- message text
    * @param component
    *   \- component name
    * @param subComponent
    *   \- sub-component name
    */
  def debug(message: String, component: String, subComponent: String): Unit

  /** Logs a WARNING severity message.
    *
    * @param message
    *   \- message text
    * @param component
    *   \- component name
    * @param subComponent
    *   \- sub-component name
    */
  def warning(message: String, component: String, subComponent: String): Unit

  /** Logs an ERROR severity message.
    *
    * @param message
    *   \- message text
    * @param component
    *   \- component name
    * @param subComponent
    *   \- sub-component name
    */
  def error(message: String, component: String, subComponent: String): Unit
}

object Logger extends Logger with TelemetryProvider {

  var delegateLogger: Logger                               = PrintLnLogger
  var delegateTelemetryProvider: Option[TelemetryProvider] = None

  def withLogger(logger: Logger): this.type = {
    delegateLogger = logger
    this
  }
  def withTelemetry(telemetry: TelemetryProvider): this.type = {
    delegateTelemetryProvider = Some(telemetry)
    this
  }

  override def log(message: String, severity: MessageSeverity, component: String, subComponent: String): Unit = {
    delegateLogger.log(message, severity, component, subComponent)
  }

  override def debug(message: String, component: String, subComponent: String): Unit = {
    delegateLogger.debug(message, component, subComponent)
  }

  override def warning(message: String, component: String, subComponent: String): Unit = {
    delegateLogger.warning(message, component, subComponent)
  }

  override def error(message: String, component: String, subComponent: String): Unit = {
    delegateLogger.error(message, component, subComponent)
  }

  override def addTimedMessage(
      code: String,
      messageType: MessageTypes,
      msg: String,
      uri: String,
      uuid: String
  ): Unit = {
    delegateTelemetryProvider.foreach(_.addTimedMessage(code, messageType, msg, uri, uuid))
  }

  override def addErrorMessage(code: String, msg: String, uri: String, uuid: String): Unit = {
    delegateTelemetryProvider.foreach(_.addErrorMessage(code, msg, uri, uuid))

  }
}
