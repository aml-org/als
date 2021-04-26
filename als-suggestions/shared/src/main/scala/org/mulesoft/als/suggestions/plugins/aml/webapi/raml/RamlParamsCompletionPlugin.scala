package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.{AmfObject, Shape}
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.ParameterModel
import amf.plugins.domain.webapi.models.{Parameter, Request}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.{
  CommonHeadersValues,
  WebApiKnownValueCompletionPlugin,
  WebApiTypeFacetsCompletionPlugin
}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

import scala.concurrent.Future

// TODO: new traverse check deletion
abstract class RamlParamsCompletionPlugin(typeFacetsCompletionPlugin: WebApiTypeFacetsCompletionPlugin,
                                          withOthers: Seq[RawSuggestion] = Nil)
    extends AMLCompletionPlugin {
  override def id: String = "RamlParamsCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future.successful(computeSuggestions(params))

  private def computeSuggestions(params: AmlCompletionRequest) = {
    if (params.yPartBranch.isKey) {
      params.amfObject match {
        case param: Parameter if isNotName(params) && param.fields.getValueAsOption(ParameterModel.Schema).isDefined =>
          computeParam(param, params.branchStack, typeFacetsCompletionPlugin)
        case _: Shape if params.branchStack.headOption.exists(_.isInstanceOf[Parameter]) =>
          withOthers
        case _ => Nil
      }
    } else Nil
  }

  def computeParam(param: Parameter,
                   branchStack: Seq[AmfObject],
                   typeFacetsCompletionPlugin: WebApiTypeFacetsCompletionPlugin): Seq[RawSuggestion] = {
    typeFacetsCompletionPlugin.resolveShape(param.schema, branchStack, Raml10TypesDialect()) ++ withOthers
  }

  private def isNotName(params: AmlCompletionRequest): Boolean = {
    params.fieldEntry match {
      case Some(FieldEntry(field, value)) => field != ParameterModel.Name
      case _                              => true
    }
  }
}
