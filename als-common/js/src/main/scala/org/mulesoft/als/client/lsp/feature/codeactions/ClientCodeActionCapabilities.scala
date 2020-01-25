package org.mulesoft.als.client.lsp.feature.codeactions

import org.mulesoft.lsp.feature.codeactions.CodeActionCapabilities

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCodeActionCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean]                             = js.native
  def codeActionLiteralSupport: ClientCodeActionLiteralSupportCapabilities = js.native
}

object ClientCodeActionCapabilities {
  def apply(internal: CodeActionCapabilities): ClientCodeActionCapabilities =
    js.Dynamic
      .literal(dynamicRegistration = internal.dynamicRegistration.orUndefined,
               codeActionLiteralSupport = internal.codeActionLiteralSupport.toClient)
      .asInstanceOf[ClientCodeActionCapabilities]
}

// $COVERAGE-ON$