package org.mulesoft.als.server

import org.mulesoft.als.server.client.{AlsClientNotifier, ClientConnection, ClientNotifier}
import org.mulesoft.lsp.client.{AlsLanguageClientAware, LspLanguageClientAware}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("ClientNotifierFactory")
object ClientNotifierFactory {
  def createWithClientAware(logger: ClientLogger)
    : ClientNotifier with LspLanguageClientAware with AlsClientNotifier[js.Any] with AlsLanguageClientAware[js.Any] =
    ClientConnection[js.Any](ClientLoggerAdapter(logger))
}
