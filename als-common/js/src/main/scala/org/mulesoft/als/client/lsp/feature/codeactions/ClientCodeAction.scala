package org.mulesoft.als.client.lsp.feature.codeactions

import org.mulesoft.als.client.lsp.command.ClientCommand
import org.mulesoft.als.client.lsp.edit.ClientWorkspaceEdit
import org.mulesoft.als.client.lsp.feature.diagnostic.ClientDiagnostic

import scala.scalajs.js

@js.native
trait ClientCodeAction extends js.Object {
  def title: String = js.native

  def kind: js.UndefOr[String] = js.native

  def diagnostics: js.UndefOr[js.Array[ClientDiagnostic]] = js.native

  def edit: js.UndefOr[ClientWorkspaceEdit] = js.native

  def command: js.UndefOr[ClientCommand] = js.native
}