package org.mulesoft.als.suggestions.plugins.aml

import amf.core.annotations.SourceAST
import amf.core.model.document.Document
import org.mulesoft.als.common.YamlUtils
import org.mulesoft.als.suggestions.interfaces.{CompletionParams, CompletionPlugin, RawSuggestion}
import org.mulesoft.lsp.edit.TextEdit
import org.yaml.model.YPart

import scala.concurrent.Future

class AMLEnumCompletions(params: CompletionParams, ast: Option[YPart]) extends AMLSuggestionsHelper {
  private def getSuggestions: Seq[String] =
    params.fieldEntry
      .flatMap(e => {
        params.propertyMappings
          .find(
            pm =>
              pm.fields
                .fields()
                .exists(f => f.value.toString == e.field.value.iri()))
          .map(pm =>
            pm.enum()
              .flatMap(_.option().map(e => {
                if (pm.allowMultiple().value() && params.prefix.isEmpty && !YamlUtils
                      .isArray(ast, params.position.moveLine(1)))
                  s"\n${getIndentation(params.currentBaseUnit, params.position)}- ${e.toString}"
                else e.toString
              })))
      })
      .getOrElse(Nil)

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
      case d: Document =>
        d.encodes.annotations.find(classOf[SourceAST]).map(_.ast)
      case bu => bu.annotations.find(classOf[SourceAST]).map(_.ast)
    }

    if (!YamlUtils.isKey(ast, params.position.moveLine(1)))
      new AMLEnumCompletions(params, ast).resolve()
    else Future.successful(Seq())
  }
}
