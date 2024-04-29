package org.mulesoft.lsp.feature.link

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDocumentLinkOptions extends js.Object {
  def resolveProvider: UndefOr[Boolean] = js.native
}

object ClientDocumentLinkOptions {
  def apply(internal: DocumentLinkOptions): ClientDocumentLinkOptions =
    js.Dynamic
      .literal(resolveProvider = internal.resolveProvider.orUndefined)
      .asInstanceOf[ClientDocumentLinkOptions]
}

// $COVERAGE-ON$
