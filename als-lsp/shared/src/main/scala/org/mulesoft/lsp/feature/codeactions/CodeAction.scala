package org.mulesoft.lsp.feature.codeactions

import org.mulesoft.lsp.edit.WorkspaceEdit
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind
import org.mulesoft.lsp.feature.command.Command
import org.mulesoft.lsp.feature.diagnostic.Diagnostic

/** @param title
  *   A short, human-readable, title for this code action.
  * @param kind
  *   The kind of the code action. Used to filter code actions.
  * @param diagnostics
  *   The diagnostics that this code action resolves.
  * @param isPreferred
  *   Marks this as a preferred action. Preferred actions are used by the `auto fix` command and can be targeted by
  *   keybindings. A quick fix should be marked preferred if it properly addresses the underlying error. A refactoring
  *   should be marked preferred if it is the most reasonable choice of actions to take.
  * @param edit
  *   The workspace edit this code action performs.
  * @param command
  *   A command this code action executes. If a code action provides an edit and a command, first the edit is executed
  *   and then the command.
  */
case class CodeAction(
    title: String,
    kind: Option[CodeActionKind],
    diagnostics: Option[Seq[Diagnostic]],
    isPreferred: Option[Boolean],
    edit: Option[WorkspaceEdit],
    command: Option[Command]
)
