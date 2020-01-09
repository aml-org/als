package org.mulesoft.als.client.lsp.feature.link

import org.mulesoft.als.client.lsp.common.ClientTextDocumentIdentifier
import org.mulesoft.lsp.feature.link.DocumentLinkParams

import scala.scalajs.js
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
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