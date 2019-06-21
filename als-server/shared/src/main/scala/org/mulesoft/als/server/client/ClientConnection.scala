package org.mulesoft.als.server.client

import org.mulesoft.als.server.logger.Logger
import org.mulesoft.lsp.client.{LanguageClient, LanguageClientAware}
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams

case class ClientConnection(logger: Logger) extends LanguageClientAware with ClientNotifier {
  var clientProxy: Option[LanguageClient] = None

  override def connect(client: LanguageClient): Unit = clientProxy = Some(client)

  override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit = applyToClient { client =>
    client.publishDiagnostic(params)
  }

  private def applyToClient(fn: LanguageClient => Unit): Unit =
    if (clientProxy.isEmpty)
      logger.warning("Client not connect", "ClientConnection", "applyToClient")
    else
      fn(clientProxy.get)
}
