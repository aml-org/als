package org.mulesoft.als.client.lsp.feature.codeactions

import org.mulesoft.lsp.feature.codeactions.CodeActionLiteralSupportCapabilities

import scala.scalajs.js
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._

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