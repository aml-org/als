package org.mulesoft.als.server.modules.telemetry

import org.mulesoft.als.server.ClientNotifierModule
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
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

  override def applyConfig(config: Option[TelemetryClientCapabilities]): Unit = {}

  override def initialize(): Future[Unit] = {
    Future.successful()
  }

  override def addTimedMessage(code: String, messageType: MessageTypes, msg: String, uri: String): Unit =
    clientNotifier.notifyTelemetry(TelemetryMessage(code, messageType.id, msg, uri, System.currentTimeMillis()))
}
