package org.mulesoft.lsp.feature.documenthighlight

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.common.{ClientPosition, ClientTextDocumentIdentifier}
import org.mulesoft.lsp.feature.highlight.DocumentHighlightParams

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDocumentHighlightParams extends js.Object {
  def textDocument: ClientTextDocumentIdentifier = js.native
  def position: ClientPosition                   = js.native
}

object ClientDocumentHighlightParams {
  def apply(internal: DocumentHighlightParams): ClientDocumentHighlightParams =
    js.Dynamic
      .literal(textDocument = internal.textDocument.toClient, position = internal.position.toClient)
      .asInstanceOf[ClientDocumentHighlightParams]
}

// $COVERAGE-ON$
