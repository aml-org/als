package org.mulesoft.als.actions.codeactions.plugins

import org.mulesoft.als.common.edits.AbstractWorkspaceEdit
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind

trait CodeActionKindTitle {
  val kind: CodeActionKind
  val title: String

  def baseCodeAction(edit: AbstractWorkspaceEdit): AbstractCodeAction =
    AbstractCodeAction(title, Some(kind), Some(false), Some(edit))
}
