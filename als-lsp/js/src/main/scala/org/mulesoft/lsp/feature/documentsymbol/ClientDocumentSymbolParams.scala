package org.mulesoft.lsp.feature.documentsymbol

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.common.ClientTextDocumentIdentifier

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

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

// $COVERAGE-ON$
