package org.mulesoft.lsp.feature.typedefinition

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientTypeDefinitionClientCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean] = js.native
  def linkSupport: js.UndefOr[Boolean]         = js.native
}

object ClientTypeDefinitionClientCapabilities {
  def apply(internal: TypeDefinitionClientCapabilities): ClientTypeDefinitionClientCapabilities =
    js.Dynamic
      .literal(
        dynamicRegistration = internal.dynamicRegistration.orUndefined,
        linkSupport = internal.linkSupport.orUndefined
      )
      .asInstanceOf[ClientTypeDefinitionClientCapabilities]
}
// $COVERAGE-ON$
