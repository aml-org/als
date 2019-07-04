package org.mulesoft.als.suggestions

import amf.core.annotations.SourceAST
import amf.core.model.document.BaseUnit
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.common.YamlUtils
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.suggestions.interfaces._
import org.yaml.model.{YNode, YPart}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class CompletionProviderAST(request: CompletionRequest) extends CompletionProvider {

  private def extractText(node: YNode, position: Position): String =
    node
      .as[String]
      .lines
      .toList(position.line + 1 - node.range.lineFrom)
      .substring(0, position.column - node.range.columnFrom)

  private def getPrefix(ast: Option[YPart], position: Position): String =
    ast
      .map(YamlUtils.getNodeByPosition(_, Position(position.line + 1, position.column)))
      .map {
        case node: YNode => extractText(node, position)
        case _           => ""
      }
      .getOrElse("")

  private def filteredSuggestions(allSuggestions: Seq[RawSuggestion], prefix: String): Seq[RawSuggestion] =
    allSuggestions.filter(s => s.displayText.startsWith(prefix) || s.newText.startsWith(prefix))

  override def suggest(): Future[Seq[Suggestion]] = {
    val linePrefix =
      getPrefix(request.baseUnit.annotations.find(classOf[SourceAST]).map(sAST => sAST.ast), request.position)
    CompletionPluginsRegistryAML
      .pluginSuggestions(new CompletionParams {
        override val currentBaseUnit: BaseUnit              = request.baseUnit
        override val position: Position                     = request.position
        override val prefix: String                         = linePrefix
        override val propertyMappings: Seq[PropertyMapping] = request.propertyMapping
        override val fieldEntry: Option[FieldEntry]         = request.fieldEntry
      })
      .map(suggestions => {
        filteredSuggestions(suggestions, linePrefix)
          .map(rawSuggestion =>
            new Suggestion {
              override def text: String = rawSuggestion.newText + rawSuggestion.whiteSpacesEnding

              override def description: String = rawSuggestion.description

              override def displayText: String = rawSuggestion.displayText

              override def prefix: String = linePrefix

              override def category: String = "" // TODO: Category for AML?

              override def trailingWhitespace: String = rawSuggestion.whiteSpacesEnding

              override def range: Option[PositionRange] = None
          })
      })
  }
}

object CompletionProviderAST {
  def apply(request: CompletionRequest): CompletionProviderAST = new CompletionProviderAST(request)
}
