package org.mulesoft.lsp.feature.codeactions

import scala.scalajs.js
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCodeActionLiteralSupportCapabilities extends js.Object {
  def codeActionKind: ClientCodeActionKindCapabilities = js.native
}

object ClientCodeActionLiteralSupportCapabilities {
  def apply(internal: CodeActionLiteralSupportCapabilities): ClientCodeActionLiteralSupportCapabilities =
    js.Dynamic
      .literal(codeActionKind = internal.codeActionKind.toClient)
      .asInstanceOf[ClientCodeActionLiteralSupportCapabilities]
}
// $COVERAGE-ON$
