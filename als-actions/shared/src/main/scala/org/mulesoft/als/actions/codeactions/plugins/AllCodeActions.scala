package org.mulesoft.als.actions.codeactions.plugins

import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.conversions.{JsonSchemaToRamlType, RamlTypeToJsonSchema}
import org.mulesoft.als.actions.codeactions.plugins.declarations.`trait`.ExtractTraitCodeAction
import org.mulesoft.als.actions.codeactions.plugins.declarations.delete.DeleteDeclaredNodeCodeAction
import org.mulesoft.als.actions.codeactions.plugins.declarations.fragment.{ExtractRamlDeclarationToFragmentCodeAction, ExtractRamlTypeToFragmentCodeAction}
import org.mulesoft.als.actions.codeactions.plugins.declarations.library.ExtractRamlToLibraryCodeAction
import org.mulesoft.als.actions.codeactions.plugins.declarations.resourcetype.ExtractResourceTypeCodeAction
import org.mulesoft.als.actions.codeactions.plugins.declarations.samefile.{ExtractElementCodeAction, ExtractRamlTypeCodeAction}
import org.mulesoft.als.actions.codeactions.plugins.vocabulary.{ExternalVocabularyToLocalCodeAction, SynthesizeVocabularyCodeAction}

import scala.collection.mutable

object AllCodeActions {
  def all: Seq[CodeActionFactory] = base ++ CustomCodeActions.custom

  def base: Seq[CodeActionFactory] =
    Seq(
      ExtractElementCodeAction,
      ExtractRamlTypeCodeAction,
      ExtractRamlDeclarationToFragmentCodeAction,
      ExtractRamlTypeToFragmentCodeAction,
      DeleteDeclaredNodeCodeAction,
      ExtractRamlToLibraryCodeAction,
      RamlTypeToJsonSchema,
      JsonSchemaToRamlType,
      ExtractResourceTypeCodeAction,
      ExtractTraitCodeAction,
      SynthesizeVocabularyCodeAction,
      ExternalVocabularyToLocalCodeAction
    )  // TestCodeAction
}

object CustomCodeActions {
  private val innerCustom: mutable.Set[CodeActionFactory] = mutable.Set()

  def custom: Set[CodeActionFactory] = innerCustom.toSet

  def addCustom(codeActionFactory: CodeActionFactory): Unit = innerCustom.add(codeActionFactory)

  def clear(): Unit = innerCustom.clear()
}
