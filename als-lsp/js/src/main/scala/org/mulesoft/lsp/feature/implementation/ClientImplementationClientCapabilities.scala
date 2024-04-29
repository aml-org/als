package org.mulesoft.lsp.feature.implementation

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientImplementationClientCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean] = js.native
  def linkSupport: js.UndefOr[Boolean]         = js.native
}

object ClientImplementationClientCapabilities {
  def apply(internal: ImplementationClientCapabilities): ClientImplementationClientCapabilities =
    js.Dynamic
      .literal(
        dynamicRegistration = internal.dynamicRegistration.orUndefined,
        linkSupport = internal.linkSupport.orUndefined
      )
      .asInstanceOf[ClientImplementationClientCapabilities]
}

// $COVERAGE-ON$
