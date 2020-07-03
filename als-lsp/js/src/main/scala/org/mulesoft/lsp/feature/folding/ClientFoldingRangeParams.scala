package org.mulesoft.lsp.feature.folding

import org.mulesoft.lsp.feature.common.ClientTextDocumentIdentifier

import scala.scalajs.js
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientFoldingRangeParams extends js.Object {
  def textDocument: ClientTextDocumentIdentifier = js.native
}

object ClientFoldingRangeParams {
  def apply(internal: FoldingRangeParams): ClientFoldingRangeParams =
    js.Dynamic
      .literal(textDocument = internal.textDocument.toClient)
      .asInstanceOf[ClientFoldingRangeParams]
}

// $COVERAGE-ON$
