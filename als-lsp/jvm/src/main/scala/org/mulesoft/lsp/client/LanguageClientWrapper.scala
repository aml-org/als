package org.mulesoft.lsp.client

import org.eclipse.lsp4j.services.LanguageClient
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.mulesoft.lsp.Lsp4JConversions.lsp4JPublishDiagnosticsParams

case class LanguageClientWrapper(private val inner: LanguageClient) extends LspLanguageClient {
  override def publishDiagnostic(params: PublishDiagnosticsParams): Unit =
    inner.publishDiagnostics(params)

  override def notifyTelemetry(params: TelemetryMessage): Unit =
    inner.telemetryEvent(params)
}
