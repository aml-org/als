package org.mulesoft.lsp.feature.link

import scala.scalajs.js
import org.mulesoft.lsp.convert.LspConvertersSharedToClient.ClientTextDocumentIdentifierConverter
import org.mulesoft.lsp.feature.common.ClientTextDocumentIdentifier
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDocumentLinkParams extends js.Object {
  def textDocument: ClientTextDocumentIdentifier = js.native
}

object ClientDocumentLinkParams {
  def apply(internal: DocumentLinkParams): ClientDocumentLinkParams =
    js.Dynamic
      .literal(textDocument = internal.textDocument.toClient)
      .asInstanceOf[ClientDocumentLinkParams]
}

// $COVERAGE-ON$
