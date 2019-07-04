package org.mulesoft.lsp.feature.codeactions

import org.mulesoft.lsp.command.Command
import org.mulesoft.lsp.edit.WorkspaceEdit
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind
import org.mulesoft.lsp.feature.diagnostic.Diagnostic

/**
  * A code action represents a change that can be performed in code, e.g. to fix a problem or
  * to refactor code.
  *
  * A CodeAction must set either `edit` and/or a `command`. If both are supplied, the `edit` is applied first, then the `command` is executed.
  */
case class CodeAction(

  /**
    * A short, human-readable, title for this code action.
    */
  title: String,

  /**
    * The kind of the code action.
    *
    * Used to filter code actions.
    */
  kind: Option[CodeActionKind] = None,

  /**
    * The diagnostics that this code action resolves.
    */
  diagnostics: Option[Seq[Diagnostic]] = None,

  /**
    * The workspace edit this code action performs.
    */
  edit: Option[WorkspaceEdit] = None,

  /**
    * A command this code action executes. If a code action
    * provides an edit and a command, first the edit is
    * executed and then the command.
    */
  command: Option[Command] = None
)