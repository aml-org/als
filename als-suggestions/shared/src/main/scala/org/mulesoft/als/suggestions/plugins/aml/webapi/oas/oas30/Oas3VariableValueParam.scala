package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.apicontract.internal.metamodel.domain.{ParameterModel, ServerModel}
import amf.core.client.scala.model.domain.Shape
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.VariableValueParam
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.{DialectNode, Oas30VariableObject}

object Oas3VariableValueParam extends VariableValueParam {

  override def applies(request: AmlCompletionRequest): Boolean =
    super.applies(request) ||
      (request.amfObject.isInstanceOf[Shape] && isParamInServer(request))

  private def isParamInServer(request: AmlCompletionRequest) =
    request.branchStack.headOption.exists(_.meta == ParameterModel) && request.branchStack.exists(e =>
      e.meta == ServerModel
    ) && !DiscriminatorObject.applies(request)

  override protected val variableDialectNode: DialectNode = Oas30VariableObject
}
