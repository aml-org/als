package org.mulesoft.als.client.lsp.feature.codeactions

import org.mulesoft.als.client.lsp.command.ClientCommand
import org.mulesoft.als.client.lsp.edit.ClientWorkspaceEdit
import org.mulesoft.als.client.lsp.feature.diagnostic.ClientDiagnostic
import org.mulesoft.lsp.feature.codeactions.CodeAction

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._

@js.native
trait ClientCodeAction extends js.Object {
  def title: String = js.native

  def kind: js.UndefOr[Int] = js.native

  def diagnostics: js.UndefOr[js.Array[ClientDiagnostic]] = js.native

  def edit: js.UndefOr[ClientWorkspaceEdit] = js.native

  def command: js.UndefOr[ClientCommand] = js.native
}

object ClientCodeAction {
  def apply(internal: CodeAction): ClientCodeAction =
    js.Dynamic
      .literal(
        title = internal.title,
        kind = internal.kind.map(_.id).orUndefined,
        diagnostics = internal.diagnostics.map(a => a.map(_.toClient).toJSArray).orUndefined,
        edit = internal.edit.map(_.toClient).orUndefined,
        command = internal.command.map(_.toClient).orUndefined
      )
      .asInstanceOf[ClientCodeAction]
}
