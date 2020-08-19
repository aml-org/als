package org.mulesoft.als.actions.codeactions.plugins.base

import org.mulesoft.lsp.edit.WorkspaceEdit
import org.mulesoft.lsp.feature.codeactions.CodeAction
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind

trait CodeActionFactory {
  val kind: CodeActionKind
  val title: String
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin

  def baseCodeAction(edit: WorkspaceEdit): CodeAction =
    CodeAction(title, Some(kind), None, Some(false), Some(edit), None)
}
