package org.mulesoft.lsp.feature.codeactions

import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind
import org.mulesoft.lsp.feature.diagnostic.Diagnostic

/**
  * Contains additional diagnostic information about the context in which
  * a code action is run.
  */
/**
  * An array of diagnostics.
  */
/**
  * Requested kind of actions to return.
  *
  * Actions not of this kind are filtered out by the client before being shown. So servers
  * can omit computing them.
  */
case class CodeActionContext(diagnostics: Seq[Diagnostic], only: Option[Seq[CodeActionKind]])
