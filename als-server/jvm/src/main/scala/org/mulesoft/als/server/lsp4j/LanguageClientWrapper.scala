package org.mulesoft.als.server.lsp4j

import org.eclipse.lsp4j.services.LanguageClient
import Lsp4JConversions._

import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.client.{LanguageClient => lspLanguageClient}
case class LanguageClientWrapper(private val inner: LanguageClient) extends lspLanguageClient {
  override def publishDiagnostic(params: PublishDiagnosticsParams): Unit =
    inner.publishDiagnostics(params)

}
