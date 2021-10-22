package org.mulesoft.als.server.modules.telemetry

import org.mulesoft.als.server.ClientNotifierModule
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.logger.Logger
import org.mulesoft.lsp.feature.telemetry.MessageTypes.{ERROR_MESSAGE, MessageTypes}
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.telemetry.{
  TelemetryClientCapabilities,
  TelemetryConfigType,
  TelemetryMessage,
  TelemetryProvider
}

import scala.concurrent.Future

class TelemetryManager(private val clientNotifier: ClientNotifier, private val logger: Logger)
    extends ClientNotifierModule[TelemetryClientCapabilities, Unit]
    with TelemetryProvider {

  override val `type`: ConfigType[TelemetryClientCapabilities, Unit] = TelemetryConfigType

  override def applyConfig(config: Option[TelemetryClientCapabilities]): Unit = {
    // not used
  }

  override def initialize(): Future[Unit] =
    Future.successful()

  override protected def addTimedMessage(code: String,
                                         messageType: MessageTypes,
                                         msg: String,
                                         uri: String,
                                         uuid: String): Unit =
    clientNotifier.notifyTelemetry(TelemetryMessage(code, messageType, msg, uri, System.currentTimeMillis(), uuid))

  override def addErrorMessage(code: String, msg: String, uri: String, uuid: String): Unit =
    clientNotifier.notifyTelemetry(TelemetryMessage(code, ERROR_MESSAGE, msg, uri, System.currentTimeMillis(), uuid))
}
