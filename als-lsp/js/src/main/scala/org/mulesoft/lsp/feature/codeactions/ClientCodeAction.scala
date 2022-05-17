package org.mulesoft.lsp.feature.codeactions

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.edit.ClientWorkspaceEdit
import org.mulesoft.lsp.feature.command.ClientCommand
import org.mulesoft.lsp.feature.diagnostic.ClientDiagnostic

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCodeAction extends js.Object {
  def title: String = js.native

  def kind: js.UndefOr[Int] = js.native

  def diagnostics: js.UndefOr[js.Array[ClientDiagnostic]] = js.native

  def isPreferred: js.UndefOr[Boolean] = js.native

  def edit: js.UndefOr[ClientWorkspaceEdit] = js.native

  def command: js.UndefOr[ClientCommand] = js.native
}

object ClientCodeAction {
  def apply(internal: CodeAction): ClientCodeAction =
    js.Dynamic
      .literal(
        title = internal.title,
        kind = internal.kind.map(_.toString).orUndefined,
        diagnostics = internal.diagnostics.map(a => a.map(_.toClient).toJSArray).orUndefined,
        isPreferred = internal.isPreferred.orUndefined,
        edit = internal.edit.map(_.toClient).orUndefined,
        command = internal.command.map(_.toClient).orUndefined
      )
      .asInstanceOf[ClientCodeAction]
}

// $COVERAGE-ON$
