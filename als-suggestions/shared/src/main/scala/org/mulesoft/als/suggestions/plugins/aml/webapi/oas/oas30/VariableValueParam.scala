package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.core.model.domain.Shape
import amf.dialects.oas.nodes.Oas30VariableObject
import amf.plugins.domain.webapi.metamodel.{ParameterModel, ServerModel}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml._

object VariableValueParam {

  def applies(request: AmlCompletionRequest): Boolean =
    request.amfObject.isInstanceOf[Shape] && isParamInServer(request)

  def suggest(): Seq[RawSuggestion] = Oas30VariableObject.Obj.propertiesRaw().filter(_.displayText != "name")

  private def isParamInServer(request: AmlCompletionRequest) =
    request.branchStack.headOption.exists(_.meta == ParameterModel) && request.branchStack.exists(e =>
      e.meta == ServerModel)
}
