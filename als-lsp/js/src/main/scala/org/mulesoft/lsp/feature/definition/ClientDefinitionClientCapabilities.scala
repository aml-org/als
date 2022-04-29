package org.mulesoft.lsp.feature.definition

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDefinitionClientCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean] = js.native
  def linkSupport: js.UndefOr[Boolean]         = js.native
}

object ClientDefinitionClientCapabilities {
  def apply(internal: DefinitionClientCapabilities): ClientDefinitionClientCapabilities =
    js.Dynamic
      .literal(
        dynamicRegistration = internal.dynamicRegistration.orUndefined,
        linkSupport = internal.linkSupport.orUndefined
      )
      .asInstanceOf[ClientDefinitionClientCapabilities]
}

// $COVERAGE-ON$
