package org.mulesoft.als.actions.codeactions.plugins

import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.conversions.RamlTypeToJsonSchema
import org.mulesoft.als.actions.codeactions.plugins.declarations.delete.DeleteDeclaredNodeCodeAction
import org.mulesoft.als.actions.codeactions.plugins.declarations.fragment.{
  ExtractRamlDeclarationToFragmentCodeAction,
  ExtractRamlTypeToFragmentCodeAction
}
import org.mulesoft.als.actions.codeactions.plugins.declarations.library.ExtractRamlToLibraryCodeAction
import org.mulesoft.als.actions.codeactions.plugins.declarations.samefile.{
  ExtractElementCodeAction,
  ExtractRamlTypeCodeAction
}

object AllCodeActions {
  def all: Seq[CodeActionFactory] =
    Seq(
      ExtractElementCodeAction,
      ExtractRamlTypeCodeAction,
      ExtractRamlDeclarationToFragmentCodeAction,
      ExtractRamlTypeToFragmentCodeAction,
      DeleteDeclaredNodeCodeAction,
      ExtractRamlToLibraryCodeAction,
      RamlTypeToJsonSchema
    ) // TestCodeAction
}
