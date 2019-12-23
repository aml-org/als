package org.mulesoft.als.client.lsp.feature.definition

import scala.scalajs.js

@js.native
trait ClientDefinitionClientCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean] = js.native
  def linkSupport: Option[Boolean]             = js.native
}
