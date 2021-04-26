package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure

import amf.plugins.domain.webapi.metamodel.{OperationModel, RequestModel}
import amf.plugins.domain.webapi.models.{Operation, Parameter}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.common.AmfSonElementFinder._
import org.mulesoft.als.suggestions.plugins.aml.{AMLRefTagCompletionPlugin, NodeMappingWrapper}
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.Oas30ParamObject

import scala.concurrent.Future

object ResolveParameterInRequest extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    request.amfObject match {
      case o: Operation if o.graph.containsField(OperationModel.Request) =>
        val branch =
          o.request.findSon(request.position.toAmfPosition, o.location().getOrElse(""), request.actualDialect)
        if (branch.obj.isInstanceOf[Parameter] && branch.fe.isEmpty)
          applies(Future.successful(
            Oas30ParamObject.Obj.propertiesRaw(d = request.actualDialect) ++ AMLRefTagCompletionPlugin.refSuggestion))
        else notApply
      case _ => notApply
    }
  }
}
