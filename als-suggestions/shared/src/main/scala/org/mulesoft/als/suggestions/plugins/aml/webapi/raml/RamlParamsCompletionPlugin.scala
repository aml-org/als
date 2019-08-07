package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.Shape
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.ParameterModel
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.CompletionPlugin

import scala.concurrent.Future

object RamlParamsCompletionPlugin extends CompletionPlugin {
  override def id: String = "RamlParamsCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future.successful(computeSuggestions(params))

  private def computeSuggestions(params: AmlCompletionRequest) = {
    if (params.yPartBranch.isKey) {
      params.amfObject match {
        case param: Parameter if isNotName(params) =>
          RamlTypeFacetsCompletionPlugin.resolveShape(param.schema, params.branchStack) :+ RawSuggestion.forKey(
            "required")
        case shape: Shape if params.branchStack.headOption.exists(_.isInstanceOf[Parameter]) =>
          Seq(RawSuggestion.forKey("required"))
        case _ => Nil
      }
    } else Nil
  }

  private def isNotName(params: AmlCompletionRequest): Boolean = {
    params.fieldEntry match {
      case Some(FieldEntry(field, value)) => field != ParameterModel.Name
      case _                              => true
    }
  }
}
