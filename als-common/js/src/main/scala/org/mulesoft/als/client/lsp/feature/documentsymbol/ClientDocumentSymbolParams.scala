package org.mulesoft.als.client.lsp.feature.documentsymbol

import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.common.ClientTextDocumentIdentifier
import org.mulesoft.lsp.feature.documentsymbol.DocumentSymbolParams

import scala.scalajs.js

@js.native
trait ClientDocumentSymbolParams extends js.Object {
  def textDocument: ClientTextDocumentIdentifier = js.native
}

object ClientDocumentSymbolParams {
  def apply(internal: DocumentSymbolParams): ClientDocumentSymbolParams =
    js.Dynamic
      .literal(textDocument = internal.textDocument.toClient)
      .asInstanceOf[ClientDocumentSymbolParams]
}
