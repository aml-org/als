package org.mulesoft.als.actions.codeactions.plugins

import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.declarations.DeleteDeclaredNodeCodeAction
import org.mulesoft.als.actions.codeactions.plugins.declarations.fragment.{ExtractRamlDeclarationToFragmentCodeAction, ExtractRamlTypeToFragmentCodeAction}
import org.mulesoft.als.actions.codeactions.plugins.declarations.samefile.ExtractElementCodeAction
import org.mulesoft.als.actions.codeactions.plugins.declarations.samefile.ExtractRAMLTypeCodeAction

object AllCodeActions {
  def all: Seq[CodeActionFactory] =
    Seq(ExtractElementCodeAction,
        ExtractRAMLTypeCodeAction,
        ExtractRamlDeclarationToFragmentCodeAction,
        ExtractRamlTypeToFragmentCodeAction,
      DeleteDeclaredNodeCodeAction
    ) // TestCodeAction
}
