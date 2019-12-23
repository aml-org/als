package org.mulesoft.als.client.lsp.feature.codeactions

import scala.scalajs.js

@js.native
trait ClientCodeActionCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean]                             = js.native
  def codeActionLiteralSupport: ClientCodeActionLiteralSupportCapabilities = js.native
}
