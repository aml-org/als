package org.mulesoft.als.suggestions

import amf.core.annotations.SourceAST
import amf.core.model.document.{BaseUnit, EncodesModel}
import amf.core.model.domain.AmfObject
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.suggestions.interfaces._
import org.yaml.model.{YNode, YPart}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class CompletionProviderAST(request: CompletionRequest) extends CompletionProvider {

  private def extractText(node: YNode, position: Position): String = {
    val lines =
      node.value.toString // this way in order to keep quotation marks for json
      .lines.drop(position.line - node.range.lineFrom)
    if (lines.hasNext) {
      val l   = lines.next()
      val idx = position.column - node.range.columnFrom
      if (l.isEmpty || idx <= 0) ""
      else l.substring(0, l.length min idx).stripPrefix("\"")
    } else ""
  }

  private def getPrefix(ast: Option[YPart], position: Position): String =
    request.yPartBranch.map(_.node) match {
      case Some(node: YNode) => extractText(node, position)
      case _                 => ""
    }

  private def brothersAndPrefix(prefix: String)(s: RawSuggestion): Boolean =
    !request.yPartBranch.exists(ypb => ypb.isKey && (ypb.brothersKeys contains s.newText)) &&
      s.newText.startsWith(prefix)

  override def suggest(): Future[Seq[Suggestion]] = {
    lazy val maybePart: Option[YPart] = (request.baseUnit match {
      case eM: EncodesModel => eM.encodes
      case bu               => bu
    }).annotations
      .find(classOf[SourceAST])
      .map(sAST => sAST.ast)

    val linePrefix =
      getPrefix(maybePart, request.position)

    CompletionPluginsRegistryAML
      .pluginSuggestions(new CompletionParams {
        override val currentBaseUnit: BaseUnit = request.baseUnit
        override val position: Position        = request.position
        override val prefix: String            = linePrefix
        override val propertyMappings: Seq[PropertyMapping] =
          request.propertyMapping
        override val fieldEntry: Option[FieldEntry]   = request.fieldEntry
        override val actualDialect: Dialect           = request.actualDialect
        override val amfObject: AmfObject             = request.amfObject
        override val yPartBranch: Option[YPartBranch] = request.yPartBranch
      })
      .map(suggestions => {
        val grouped: Map[Boolean, Seq[(Boolean, Suggestion)]] =
          (suggestions filter brothersAndPrefix(linePrefix))
            .map(rawSuggestion =>
              (rawSuggestion.isKey, new Suggestion {
                override def text: String =
                  rawSuggestion.newText

                override def description: String = rawSuggestion.description

                override def displayText: String = rawSuggestion.displayText

                override def prefix: String = linePrefix

                override def category: String = "" // TODO: Category for AML?

                override def trailingWhitespace: String =
                  rawSuggestion.whiteSpacesEnding

                override def range: Option[PositionRange] = None
              }))
            .groupBy(_._1)
        grouped.keys.flatMap(k => request.styler(k)(grouped(k).map(_._2))).toSeq
      })
  }
}

object CompletionProviderAST {
  def apply(request: CompletionRequest): CompletionProviderAST =
    new CompletionProviderAST(request)
}
