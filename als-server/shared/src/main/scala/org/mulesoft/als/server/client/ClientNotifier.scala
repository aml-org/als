package org.mulesoft.als.server.client

import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.serialization.SerializationMessage
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait ClientNotifier {
  def notifyDiagnostic(params: PublishDiagnosticsParams): Unit

  def notifyTelemetry(params: TelemetryMessage): Unit
}

@JSExportAll
trait AlsClientNotifier[S] {

  def notifySerialization(params: SerializationMessage[S]): Unit
}
