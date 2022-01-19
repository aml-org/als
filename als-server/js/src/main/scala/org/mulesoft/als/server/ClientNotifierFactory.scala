package org.mulesoft.als.server

import org.mulesoft.als.server.client.platform.{AlsClientNotifier, ClientConnection, ClientNotifier}
import org.mulesoft.als.server.protocol.client.AlsLanguageClientAware
import org.mulesoft.lsp.client.LspLanguageClientAware

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("ClientNotifierFactory")
object ClientNotifierFactory {
  def createWithClientAware(logger: JsClientLogger)
    : ClientNotifier with LspLanguageClientAware with AlsClientNotifier[js.Any] with AlsLanguageClientAware[js.Any] =
    ClientConnection[js.Any](ClientLoggerAdapter(logger))
}
