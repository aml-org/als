package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.core.model.domain.Shape
import amf.plugins.domain.webapi.metamodel.{ParameterModel, ServerModel}
import amf.plugins.domain.webapi.models.{Parameter, Server}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.als.suggestions.plugins.aml.webapi.ExceptionPlugin
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.Oas30VariableObject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object VariableValueParam extends ExceptionPlugin {

  override def applies(request: AmlCompletionRequest): Boolean =
    (request.amfObject.isInstanceOf[Shape] && isParamInServer(request)) || (isParamWithName(request))

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (applies(request)) Future {
      Oas30VariableObject.Obj.propertiesRaw(d = request.actualDialect).filter(_.displayText != "name")
    } else
      emptySuggestion

  private def isParamInServer(request: AmlCompletionRequest) =
    request.branchStack.headOption.exists(_.meta == ParameterModel) && request.branchStack.exists(e =>
      e.meta == ServerModel) && !DiscriminatorObject.applies(request)

  private def isParamWithName(request: AmlCompletionRequest) = {
    request.amfObject match {
      case p: Parameter =>
        p.name.option().nonEmpty && request.fieldEntry.isEmpty && request.branchStack.headOption
          .exists(_.isInstanceOf[Server])
      case _ => false
    }
  }
  override def id: String = "VariableValueParam"

}
