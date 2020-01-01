package org.mulesoft.als.client.lsp.feature.definition

import org.mulesoft.lsp.feature.definition.DefinitionClientCapabilities

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

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
