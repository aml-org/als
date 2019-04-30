package org.mulesoft.als.server.lsp4j


import org.eclipse.lsp4j.services.LanguageClient
import org.mulesoft.lsp.client
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams

import Lsp4JConversions._

case class LanguageClientWrapper(private val inner: LanguageClient) extends client.LanguageClient {
  override def publishDiagnostic(params: PublishDiagnosticsParams): Unit =
    inner.publishDiagnostics(params)

}
