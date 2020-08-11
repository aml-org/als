package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.VariableValueParam
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApiVariableObject
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object Async2VariableValueParam extends VariableValueParam {
  override protected val variableDialectNode: DialectNode = AsyncApiVariableObject
}
