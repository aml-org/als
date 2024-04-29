package org.mulesoft.lsp.feature.link

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDocumentLinkClientCapabilities extends js.Object {
  def dynamicRegistration: UndefOr[Boolean] = js.native
  def tooltipSupport: UndefOr[Boolean]      = js.native
}

object ClientDocumentLinkClientCapabilities {
  def apply(internal: DocumentLinkClientCapabilities): ClientDocumentLinkClientCapabilities =
    js.Dynamic
      .literal(
        dynamicRegistration = internal.dynamicRegistration.orUndefined,
        tooltipSupport = internal.tooltipSupport.orUndefined
      )
      .asInstanceOf[ClientDocumentLinkClientCapabilities]
}

// $COVERAGE-ON$
