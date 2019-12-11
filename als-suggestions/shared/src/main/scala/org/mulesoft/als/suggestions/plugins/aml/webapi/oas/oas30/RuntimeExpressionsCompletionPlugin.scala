package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import amf.plugins.domain.webapi.models.Request
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30.runtimeexpressions.{
  ExpressionToken,
  InvalidToken,
  RuntimeExpressionParser,
  RuntimeExpressionValues
}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object RuntimeExpressionsCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "RuntimeExpressionsCompletionPlugin"

  // TODO: LinkObject, Callbacks
  private def isApplicable(request: AmlCompletionRequest): Boolean =
    request.amfObject.isInstanceOf[Request] && request.yPartBranch.isValue

  // TODO: add navigation for fragments? Known values for tokens?
  private def extractExpression(v: String): Seq[RawSuggestion] = {
    (RuntimeExpressionParser(v).tokens.filterNot(_.isInstanceOf[InvalidToken]).lastOption match {
      case None                     => RuntimeExpressionValues.expressions
      case Some(_: ExpressionToken) => RuntimeExpressionValues.sources
      case _                        => Nil
    }).map { s =>
        val pre = if (v.contains(".")) v.substring(0, v.lastIndexOf('.') + 1) else ""
        RawSuggestion(s"$pre$s", isAKey = false, "RuntimeExpression", mandatory = false)
      }
  }

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
    if (isApplicable(request))
      extractExpression(request.prefix)
    else Nil
  }
}
