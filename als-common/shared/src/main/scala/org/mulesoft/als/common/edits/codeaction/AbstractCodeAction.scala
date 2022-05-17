package org.mulesoft.als.common.edits.codeaction

import org.mulesoft.als.common.edits.AbstractWorkspaceEdit
import org.mulesoft.lsp.feature.codeactions.CodeAction
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind

case class AbstractCodeAction(
    title: String,
    kind: Option[CodeActionKind],
    isPreferred: Option[Boolean],
    edit: Option[AbstractWorkspaceEdit]
) {
  def needsWorkspaceEdit: Boolean = edit.exists(_.needsDocumentChanges)

  def toCodeAction(supportsDC: Boolean): CodeAction =
    CodeAction(title, kind, None, isPreferred, edit.map(_.toWorkspaceEdit(supportsDC)), None)
}
