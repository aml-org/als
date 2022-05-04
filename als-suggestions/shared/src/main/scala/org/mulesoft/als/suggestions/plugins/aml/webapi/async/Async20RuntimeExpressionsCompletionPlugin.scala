package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.apicontract.client.scala.model.domain.{CorrelationId, Parameter}
import amf.apicontract.internal.validation.runtimeexpression.{AsyncAPIRuntimeExpressionParser, RuntimeExpressionParser}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.AbstractRuntimeExpressionsCompletionPlugin

object Async20RuntimeExpressionsCompletionPlugin extends AbstractRuntimeExpressionsCompletionPlugin {
  override def parserObject(value: String): RuntimeExpressionParser =
    AsyncAPIRuntimeExpressionParser(value)

  override protected def appliesToField(request: AmlCompletionRequest): Boolean = request.amfObject match {
    case _ @(_: Parameter | _: CorrelationId) => request.astPartBranch.isValue
    case _                                    => false
  }
}
