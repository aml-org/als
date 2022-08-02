package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.apicontract.internal.validation.runtimeexpression.{InvalidExpressionToken, RuntimeExpressionParser}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.dialect.DialectKnowledge

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class AbstractRuntimeExpressionsCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "RuntimeExpressionsCompletionPlugin"

  protected def parserObject(value: String): RuntimeExpressionParser

  protected def isApplicable(request: AmlCompletionRequest): Boolean = {
    request.astPartBranch match {
      case yPartBranch: YPartBranch =>
        !(DialectKnowledge.isRamlInclusion(yPartBranch, request.actualDialect) ||
          DialectKnowledge.isJsonInclusion(yPartBranch, request.actualDialect)) &&
        appliesToField(request)
      case _ => false
    }
  }

  protected def appliesToField(request: AmlCompletionRequest): Boolean

  // TODO: add navigation for fragments? Known values for tokens?
  private def extractExpression(v: String): Seq[RawSuggestion] = {
    val nonExpressionPrefix = if (v.contains("{")) v.substring(0, v.lastIndexOf("{") + 1) else ""
    val parser              = parserObject(v.stripPrefix(nonExpressionPrefix))
    (parser.completeStack.filterNot(_.isInstanceOf[InvalidExpressionToken]).lastOption match {
      case Some(other) => other.possibleApplications
      case None        => parser.possibleApplications
    }).map { s =>
      val pre = v.stripSuffix(
        parser.completeStack.collectFirst { case i: InvalidExpressionToken => i }.map(_.value).getOrElse("")
      )
      val displayText = pre.concat(s).stripPrefix(nonExpressionPrefix)
      RawSuggestion(s"$pre$s", displayText, isAKey = false, "RuntimeExpression", mandatory = false)
    }
  }

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
    if (isApplicable(request))
      extractExpression(request.prefix)
    else Nil
  }
}
