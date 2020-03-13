package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.plugins.domain.webapi.models.{CorrelationId, Parameter}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.AbstractRuntimeExpressionsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.runtimeexpressions.AsyncAPIRuntimeExpressionParser
import org.mulesoft.als.suggestions.plugins.aml.webapi.runtimeexpression.RuntimeExpressionParser

object Async20RuntimeExpressionsCompletionPlugin extends AbstractRuntimeExpressionsCompletionPlugin {
  override def parserObject(value: String): RuntimeExpressionParser =
    AsyncAPIRuntimeExpressionParser(value)

  override protected def appliesToField(request: AmlCompletionRequest): Boolean = request.amfObject match {
    case _ @(_: Parameter | _: CorrelationId) => true
    case _                                    => false
  }
}
