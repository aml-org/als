package org.mulesoft.als.suggestions

import amf.core.annotations.SourceAST
import amf.core.model.document.EncodesModel
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.suggestions.interfaces._
import org.yaml.model.{YNode, YPart, YType}
import RequestToCompletionParams._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class CompletionProviderAST(request: CompletionRequest) extends CompletionProvider {

  private def extractText(node: YNode, position: Position): String =
    node.tagType match {
      case YType.Str =>
        val lines: Iterator[String] = node
          .as[String]
          .lines
          .drop(position.line - node.range.lineFrom)
        if (lines.hasNext)
          lines
            .next()
            .substring(0, position.column - node.range.columnFrom - {
              if (node.asScalar.exists(_.mark.plain)) 0 else 1 // if there is a quotation mark, adjust the range according
            })
        else ""
      case _ => ""
    }

  private def getPrefix(ast: Option[YPart], position: Position): String =
    request.yPartBranch.node match {
      case node: YNode => extractText(node, position)
      case _           => ""
    }

  private def brothersAndPrefix(prefix: String)(s: RawSuggestion): Boolean =
    !(request.yPartBranch.isKey && (request.yPartBranch.brothersKeys contains s.newText)) &&
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

    CompletionsPluginHandler
      .pluginSuggestions(request.toParams(linePrefix))
      .map(suggestions => {
        val grouped: Map[Boolean, Seq[(Boolean, Suggestion)]] =
          (suggestions filter brothersAndPrefix(linePrefix))
            .map(rawSuggestion => (rawSuggestion.isKey, rawSuggestion.toSuggestion(linePrefix)))
            .groupBy(_._1)
        grouped.keys.flatMap(k => request.styler(k)(grouped(k).map(_._2))).toSeq
      })
  }
}

object CompletionProviderAST {
  def apply(request: CompletionRequest): CompletionProviderAST =
    new CompletionProviderAST(request)
}
