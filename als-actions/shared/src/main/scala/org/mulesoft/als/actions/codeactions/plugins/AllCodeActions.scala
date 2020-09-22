package org.mulesoft.als.actions.codeactions.plugins

import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.declarations.{
  DeleteDeclaredNodeCodeAction,
  ExtractElementCodeAction,
  ExtractRAMLTypeCodeAction
}

object AllCodeActions {
  def all: Seq[CodeActionFactory] =
    Seq(ExtractElementCodeAction, ExtractRAMLTypeCodeAction, DeleteDeclaredNodeCodeAction) // TestCodeAction
}
