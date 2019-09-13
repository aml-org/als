package org.mulesoft.lsp.client

import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage

trait LanguageClient {

  def publishDiagnostic(params: PublishDiagnosticsParams): Unit

  def notifyTelemetry(params: TelemetryMessage): Unit
}
