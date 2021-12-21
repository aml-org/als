package org.mulesoft.als.server.client.platform

import org.mulesoft.als.server.feature.serialization.SerializationResult
import org.mulesoft.als.server.feature.workspace.FilesInProjectParams
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait ClientNotifier {
  def notifyDiagnostic(params: PublishDiagnosticsParams): Unit

  def notifyTelemetry(params: TelemetryMessage): Unit
}

@JSExportAll
trait AlsClientNotifier[S] {

  def notifyProjectFiles(params: FilesInProjectParams): Unit

  def notifySerialization(params: SerializationResult[S]): Unit
}
