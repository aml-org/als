package org.mulesoft.als.actions.codeactions.plugins.refactor

import org.mulesoft.lsp.edit.WorkspaceEdit
import org.mulesoft.lsp.feature.codeactions.{CodeAction, CodeActionKind}

object RefactorCodeAction {
  def apply(): (String, Option[WorkspaceEdit]) => CodeAction =
    CodeAction(_, Some(CodeActionKind.Refactor), None, _, None)
}
