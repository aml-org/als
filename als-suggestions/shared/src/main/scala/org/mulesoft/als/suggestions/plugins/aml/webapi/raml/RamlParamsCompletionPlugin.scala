package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.apicontract.client.scala.model.domain.Parameter
import amf.apicontract.internal.metamodel.domain.ParameterModel
import amf.core.client.scala.model.domain.{AmfObject, Shape}
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

import scala.concurrent.Future

abstract class RamlParamsCompletionPlugin(
    typeFacetsCompletionPlugin: WebApiTypeFacetsCompletionPlugin,
    withOthers: Seq[RawSuggestion] = Nil
) extends AMLCompletionPlugin {
  override def id: String = "RamlParamsCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future.successful(computeSuggestions(params))

  private def computeSuggestions(params: AmlCompletionRequest) = {
    if (params.astPartBranch.isKey) {
      params.amfObject match {
        case param: Parameter if isNotName(params) && param.fields.getValueAsOption(ParameterModel.Schema).isDefined =>
          computeParam(param, params.branchStack, typeFacetsCompletionPlugin)
        case _: Shape if params.branchStack.headOption.exists(_.isInstanceOf[Parameter]) =>
          withOthers
        case _ => Nil
      }
    } else Nil
  }

  def computeParam(
      param: Parameter,
      branchStack: Seq[AmfObject],
      typeFacetsCompletionPlugin: WebApiTypeFacetsCompletionPlugin
  ): Seq[RawSuggestion] = {
    typeFacetsCompletionPlugin.resolveShape(param.schema, branchStack, Raml10TypesDialect()) ++ withOthers
  }

  private def isNotName(params: AmlCompletionRequest): Boolean = {
    params.fieldEntry match {
      case Some(FieldEntry(field, value)) => field != ParameterModel.Name
      case _                              => true
    }
  }
}
