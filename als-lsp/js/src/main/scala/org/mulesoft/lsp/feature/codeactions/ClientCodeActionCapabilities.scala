package org.mulesoft.lsp.feature.codeactions

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCodeActionCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean]                                         = js.native
  def codeActionLiteralSupport: js.UndefOr[ClientCodeActionLiteralSupportCapabilities] = js.native
  def isPreferredSupport: js.UndefOr[Boolean]                                          = js.native
}

object ClientCodeActionCapabilities {
  def apply(internal: CodeActionCapabilities): ClientCodeActionCapabilities =
    js.Dynamic
      .literal(
        dynamicRegistration = internal.dynamicRegistration.orUndefined,
        codeActionLiteralSupport = internal.codeActionLiteralSupport.map(_.toClient).orUndefined,
        isPreferredSupport = internal.isPreferredSupport.orUndefined
      )
      .asInstanceOf[ClientCodeActionCapabilities]
}

// $COVERAGE-ON$
