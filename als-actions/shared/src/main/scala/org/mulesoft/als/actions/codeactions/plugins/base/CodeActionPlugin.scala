package org.mulesoft.als.actions.codeactions.plugins.base

import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind
import org.mulesoft.lsp.feature.codeactions.{CodeAction, CodeActionKind}
import org.mulesoft.lsp.feature.command.Command

trait CodeActionPlugin[T] {

  val kind: CodeActionKind = CodeActionKind.Empty

  def isApplicable(params: CodeActionRequestParams): Boolean

  def apply(params: CodeActionRequestParams): Seq[T]
}

trait CodeActionCommandPlugin[T <: Command] extends CodeActionPlugin[T]

trait CodeActionResponsePlugin extends CodeActionPlugin[CodeAction]
