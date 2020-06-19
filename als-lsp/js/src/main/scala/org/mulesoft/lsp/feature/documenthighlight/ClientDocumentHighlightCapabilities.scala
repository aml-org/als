package org.mulesoft.lsp.feature.documenthighlight

import org.mulesoft.lsp.feature.highlight.DocumentHighlightCapabilities

import scala.scalajs.js.UndefOr
import scala.scalajs.js
import js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDocumentHighlightCapabilities extends js.Object {
  def dynamicRegistration: UndefOr[Boolean] = js.native
}

object ClientDocumentHighlightCapabilities {
  def apply(internal: DocumentHighlightCapabilities): ClientDocumentHighlightCapabilities =
    js.Dynamic
      .literal(dynamicRegistration = internal.dynamicRegistration.orUndefined)
      .asInstanceOf[ClientDocumentHighlightCapabilities]
}
// $COVERAGE-ON$
