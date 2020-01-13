package org.mulesoft.als.server

import org.mulesoft.als.server.client.{ClientConnection, ClientNotifier}
import org.mulesoft.lsp.client.LanguageClientAware

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("ClientNotifierFactory")
object ClientNotifierFactory {
  def createWithClientAware(logger: ClientLogger): ClientNotifier with LanguageClientAware = ClientConnection(ClientLoggerAdapter(logger))
}
