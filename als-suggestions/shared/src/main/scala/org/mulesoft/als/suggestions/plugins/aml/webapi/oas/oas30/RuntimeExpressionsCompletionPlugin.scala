package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.plugins.domain.webapi.models.Request
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30.runtimeexpressions.{
  InvalidExpressionToken,
  OASRuntimeExpressionParser
}
import org.mulesoft.amfmanager.dialect.DialectKnowledge

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RuntimeExpressionsCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "RuntimeExpressionsCompletionPlugin"

  // TODO: LinkObject, Callbacks
  private def isApplicable(request: AmlCompletionRequest): Boolean =
    !(DialectKnowledge.isRamlInclusion(request.yPartBranch, request.actualDialect) ||
      DialectKnowledge.isJsonInclusion(request.yPartBranch, request.actualDialect)) &&
      request.amfObject.isInstanceOf[Request] && request.yPartBranch.isValue

  // TODO: add navigation for fragments? Known values for tokens?
  private def extractExpression(v: String): Seq[RawSuggestion] = {
    val parser = OASRuntimeExpressionParser(v)
    (parser.completeStack.filterNot(_.isInstanceOf[InvalidExpressionToken]).lastOption match {
      case Some(other) => other.possibleApplications
      case None        => parser.possibleApplications
    }).map { s =>
      val pre = v.stripSuffix(
        parser.completeStack.collectFirst { case i: InvalidExpressionToken => i }.map(_.value).getOrElse(""))
      RawSuggestion(s"$pre$s", isAKey = false, "RuntimeExpression", mandatory = false)
    }
  }

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
    if (isApplicable(request))
      extractExpression(request.prefix)
    else Nil
  }
}
