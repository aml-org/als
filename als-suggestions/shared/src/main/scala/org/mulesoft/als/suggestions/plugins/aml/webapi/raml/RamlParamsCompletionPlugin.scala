package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.{AmfObject, Shape}
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.ParameterModel
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin

import scala.concurrent.Future

abstract class RamlParamsCompletionPlugin(typeFacetsCompletionPlugin: WebApiTypeFacetsCompletionPlugin,
                                          withOthers: Seq[RawSuggestion] = Nil)
    extends AMLCompletionPlugin {
  override def id: String = "RamlParamsCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future.successful(computeSuggestions(params))

  private def computeSuggestions(params: AmlCompletionRequest) = {
    if (params.yPartBranch.isKey) {
      params.amfObject match {
        case param: Parameter if isNotName(params) =>
          computeParam(param, params.branchStack, params.indentation, typeFacetsCompletionPlugin)
        case _: Shape if params.branchStack.headOption.exists(_.isInstanceOf[Parameter]) =>
          withOthers
        case _ => Nil
      }
    } else Nil
  }

  def computeParam(param: Parameter,
                   branchStack: Seq[AmfObject],
                   indentation: String,
                   typeFacetsCompletionPlugin: WebApiTypeFacetsCompletionPlugin): Seq[RawSuggestion] = {
    typeFacetsCompletionPlugin.resolveShape(param.schema, branchStack, indentation) ++ withOthers
  }

  private def isNotName(params: AmlCompletionRequest): Boolean = {
    params.fieldEntry match {
      case Some(FieldEntry(field, value)) => field != ParameterModel.Name
      case _                              => true
    }
  }
}
