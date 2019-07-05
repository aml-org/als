package org.mulesoft.als.suggestions.plugins.aml

import amf.core.annotations.SourceAST
import amf.core.model.document.Document
import org.mulesoft.als.common.YamlUtils
import org.mulesoft.als.common.YamlUtils.{getNodeByPosition, getParents}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.interfaces.{
  CompletionParams,
  CompletionPlugin,
  RawSuggestion
}
import org.mulesoft.lsp.edit.TextEdit
import org.yaml.model.YPart

import scala.concurrent.Future

class AMLEnumCompletions(params: CompletionParams,
                         ast: Option[YPart],
                         parents: Seq[YPart])
    extends AMLSuggestionsHelper {

  def presentArray(value: String,
                   parents: Seq[YPart],
                   amfPosition: Position): String = {
//    if (YamlUtils.isInArray(parents, amfPosition))
//      value
//    else
      s"\n${getIndentation(params.currentBaseUnit, params.position)}- ${value}"
  }

  private def getSuggestions: Seq[String] =
    params.propertyMappings.headOption
      .map(
        pm =>
          pm.enum()
            .flatMap(_.option().map(e => {
              val amfPosition: Position = params.position.moveLine(1)
              val selectedNode: Option[YPart] =
                ast.map(getNodeByPosition(_, amfPosition))
              if (pm.allowMultiple()
                    .value() && params.prefix.isEmpty && !YamlUtils
                    .isArray(selectedNode, amfPosition) && !YamlUtils
                    .isInArray(parents, amfPosition)) {
                presentArray(e.toString, parents, amfPosition)
              } else e.toString
            })))
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

    ast
      .map(a => {
        val amfPosition = params.position.moveLine(1)
        val parents = getParents(a, amfPosition, Seq())
        if (YamlUtils.isInArray(parents, amfPosition) || !YamlUtils
              .isKey(parents.headOption, amfPosition))
          new AMLEnumCompletions(params, ast, parents).resolve()
        else
          Future.successful(Nil)
      }).getOrElse(Future.successful(Nil))
  }
}
