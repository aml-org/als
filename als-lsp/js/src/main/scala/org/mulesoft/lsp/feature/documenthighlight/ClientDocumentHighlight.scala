package org.mulesoft.lsp.feature.documenthighlight

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.common.ClientRange
import org.mulesoft.lsp.feature.highlight.DocumentHighlight

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDocumentHighlight extends js.Object {
  def range: ClientRange = js.native
  def kind: Int          = js.native
}

object ClientDocumentHighlight {
  def apply(internal: DocumentHighlight): ClientDocumentHighlight =
    js.Dynamic
      .literal(range = internal.range.toClient, kind = internal.kind.id)
      .asInstanceOf[ClientDocumentHighlight]
}
// $COVERAGE-ON$
