package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import org.mulesoft.als.suggestions.plugins.aml.webapi.AbstractRuntimeExpressionsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.runtimeexpressions.AsyncAPIRuntimeExpressionParser
import org.mulesoft.als.suggestions.plugins.aml.webapi.runtimeexpression.RuntimeExpressionParser

object AsyncAPIRuntimeExpressionsCompletionPlugin extends AbstractRuntimeExpressionsCompletionPlugin {
  override def parserObject(value: String): RuntimeExpressionParser = AsyncAPIRuntimeExpressionParser(value)
}