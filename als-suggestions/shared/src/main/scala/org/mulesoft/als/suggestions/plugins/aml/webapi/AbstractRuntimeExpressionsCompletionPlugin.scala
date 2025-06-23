package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.apicontract.internal.validation.runtimeexpression.{InvalidExpressionToken, RuntimeExpressionParser}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.suggestions.{PlainText, RawSuggestion, SuggestionStructure}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.dialect.DialectKnowledge
import org.mulesoft.common.client.lexical.ASTElement

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class AbstractRuntimeExpressionsCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "RuntimeExpressionsCompletionPlugin"

  protected def parserObject(value: String): RuntimeExpressionParser

  protected def isApplicable(request: AmlCompletionRequest): Boolean =
    request.astPartBranch match {
      case yPartBranch: YPartBranch =>
        !(DialectKnowledge.isRamlInclusion(yPartBranch, request.actualDocumentDefinition) ||
          DialectKnowledge.isJsonInclusion(yPartBranch, request.actualDocumentDefinition)) &&
        appliesToField(request)
      case _ => false
    }

  protected def appliesToField(request: AmlCompletionRequest): Boolean

  // TODO: add navigation for fragments? Known values for tokens?
  private def extractExpression(v: String): Seq[RawSuggestion] = {
    val nonExpressionPrefix = v.substring(0, v.lastIndexOf("{") + 1)
    val parser              = parserObject(v.stripPrefix(nonExpressionPrefix))
    (parser.completeStack.filterNot(_.isInstanceOf[InvalidExpressionToken]).lastOption match {
      case Some(other) => other.possibleApplications
      case None        => parser.possibleApplications
    }).map { s =>
      val pre = v.stripSuffix(
        parser.completeStack.collectFirst { case i: InvalidExpressionToken => i }.map(_.value).getOrElse("")
      )
      val displayText = pre.concat(s).stripPrefix(nonExpressionPrefix)
      val suffix      = getSuffixForExpression(v, nonExpressionPrefix)
      RawSuggestion(s"$pre$s$suffix", displayText, isAKey = false, "RuntimeExpression", mandatory = false)
    }
  }

  protected def getSuffixForExpression(v: String, nonExpressionPrefix: String): String = ""

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
    if (isApplicable(request))
      extractExpression(request.prefix)
    else Nil
  }
}
