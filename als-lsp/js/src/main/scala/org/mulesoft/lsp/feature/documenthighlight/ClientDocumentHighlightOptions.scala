package org.mulesoft.lsp.feature.documenthighlight

import org.mulesoft.lsp.feature.highlight.DocumentHighlightOptions

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDocumentHighlightOptions extends js.Object

object ClientDocumentHighlightOptions {
  def apply(internal: DocumentHighlightOptions): ClientDocumentHighlightOptions =
    js.Dynamic
      .literal()
      .asInstanceOf[ClientDocumentHighlightOptions]
}
// $COVERAGE-ON$