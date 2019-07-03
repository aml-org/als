package org.mulesoft.lsp.client

import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams

trait LanguageClient {

  def publishDiagnostic(params: PublishDiagnosticsParams): Unit
}
