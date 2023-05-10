package org.mulesoft.als.server.client

import org.mulesoft.als.server.feature.serialization.SerializationResult
import org.mulesoft.als.server.feature.workspace.FilesInProjectParams
import org.mulesoft.als.server.protocol.client.AlsLanguageClient
import org.mulesoft.lsp.Lsp4JConversions.lsp4JPublishDiagnosticsParams
import org.mulesoft.lsp.client.LspLanguageClient
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage

import java.io.StringWriter

case class AlsLanguageClientWrapper(private val inner: AlsLanguageClientExtensions)
    extends LspLanguageClient
    with AlsLanguageClient[StringWriter] {
  override def publishDiagnostic(params: PublishDiagnosticsParams): Unit =
    inner.publishDiagnostics(params)

  override def notifyTelemetry(params: TelemetryMessage): Unit =
    inner.telemetryEvent(params)

  override def notifySerialization(params: SerializationResult[StringWriter]): Unit = inner.publishSerialization(params)

  override def notifyProjectFiles(params: FilesInProjectParams): Unit = inner.publishProjectFiles(params)
}
