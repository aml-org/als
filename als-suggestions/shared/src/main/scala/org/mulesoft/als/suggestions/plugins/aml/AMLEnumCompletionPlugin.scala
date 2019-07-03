package org.mulesoft.als.suggestions.plugins.aml

import amf.core.annotations.SourceAST
import amf.core.model.document.Document
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.vocabularies.model.document.{Dialect, DialectInstance}
import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{AmfUtils, YamlUtils}
import org.mulesoft.als.suggestions.DialectRegistry
import org.mulesoft.als.suggestions.interfaces.{CompletionParams, CompletionPlugin, RawSuggestion}
import org.mulesoft.lsp.edit.TextEdit

import scala.concurrent.Future

class AMLEnumCompletions(params: CompletionParams) extends AMLSuggestionsHelper {
  private def getSuggestions: Seq[String] = {
    val dialect: Dialect =
      params.currentBaseUnit.sourceVendor.flatMap(v => DialectRegistry.get(v.name).headOption) match {
        case Some(d) => d
        case _ =>
          params.currentBaseUnit match {
            case d: DialectInstance =>
              AMLPlugin.registry
                .dialectFor(d)
                .getOrElse(throw new Exception(s"No Dialect for ${params.currentBaseUnit.id} found"))
          }
      }

    val entry =
      AmfUtils.getFieldEntryByPosition(params.node, Position(params.position.line + 1, params.position.column))

    val maybeMapping = getDialectNode(dialect, params.node) match {
      case Some(nm: NodeMapping) =>
        entry.flatMap(e =>
          nm.propertiesMapping().find(pm => pm.fields.fields().exists(f => f.value.toString == e.field.value.iri())))
      case _ => None
    }
    maybeMapping match {
      case Some(pm) =>
        pm.enum().flatMap(_.option().map(_.toString))
      case _ => Nil
    }
  }

  def resolve(): Future[Seq[RawSuggestion]] =
    Future.successful(
      getSuggestions
        .map(s =>
          new RawSuggestion {
            override def newText: String = s

            override def displayText: String = s

            override def description: String = s

            override def textEdits: Seq[TextEdit] = Seq()

            override def whiteSpacesEnding: String = ""
        }))
}

object AMLEnumCompletionPlugin extends CompletionPlugin {
  override def id = "AMLEnumCompletionPlugin"

  override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] = {
    val ast = params.currentBaseUnit match {
      case d: Document => d.encodes.annotations.find(classOf[SourceAST]).map(_.ast)
      case bu          => bu.annotations.find(classOf[SourceAST]).map(_.ast)
    }
    val amfPosition = Position(params.position.line + 1, params.position.column)
    if (!YamlUtils.isKey(ast, amfPosition))
      new AMLEnumCompletions(params).resolve()
    else Future.successful(Seq())
  }
}
