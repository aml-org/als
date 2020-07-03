package org.mulesoft.lsp.feature.hover

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientHoverClientCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean] = js.native
  def contentFormat: js.Array[String]          = js.native
}

object ClientHoverClientCapabilities {
  def apply(internal: HoverClientCapabilities): ClientHoverClientCapabilities =
    js.Dynamic
      .literal(
        dynamicRegistration = internal.dynamicRegistration.orUndefined,
        contentFormat = internal.contentFormat.toJSArray
      )
      .asInstanceOf[ClientHoverClientCapabilities]
}
// $COVERAGE-ON$
