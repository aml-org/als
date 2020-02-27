package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import org.mulesoft.als.suggestions.plugins.aml.webapi.AbstractRuntimeExpressionsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30.runtimeexpressions.OASRuntimeExpressionParser
import org.mulesoft.als.suggestions.plugins.aml.webapi.runtimeexpression.RuntimeExpressionParser

object OasRuntimeExpressionsCompletionPlugin extends AbstractRuntimeExpressionsCompletionPlugin {
  override def parserObject(value: String): RuntimeExpressionParser = OASRuntimeExpressionParser(value)
}