package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure

import amf.apicontract.client.scala.model.domain.{Operation, Parameter}
import amf.apicontract.internal.metamodel.domain.OperationModel
import org.mulesoft.als.common.AmfSonElementFinder.AlsAmfObject
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml.{AMLRefTagCompletionPlugin, NodeMappingWrapper}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.Oas30ParamObject

import scala.concurrent.Future

object ResolveParameterInRequest extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    request.amfObject match {
      case o: Operation if o.graph.containsProperty(OperationModel.Request.value.iri()) =>
        val branch =
          o.request.findSon(o.location().getOrElse(""), request.actualDialect, request.astPartBranch)
        if (branch.obj.isInstanceOf[Parameter] && branch.fe.isEmpty)
          applies(
            Future.successful(
              Oas30ParamObject.Obj
                .propertiesRaw(fromDialect = request.actualDialect) ++ AMLRefTagCompletionPlugin.refSuggestion
            )
          )
        else notApply
      case _ => notApply
    }
  }
}
