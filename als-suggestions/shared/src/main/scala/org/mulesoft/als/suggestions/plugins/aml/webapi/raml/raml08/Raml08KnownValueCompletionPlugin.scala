package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml08

import amf.plugins.domain.shapes.metamodel.ScalarShapeModel
import amf.plugins.domain.webapi.metamodel.ParameterModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiKnownValueCompletionPlugin

import scala.concurrent.Future

object Raml08KnownValueCompletionPlugin extends WebApiKnownValueCompletionPlugin {

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (isShapeParam(params) && isHeader(params))
      innerResolver(params, ParameterModel.Name, ParameterModel.`type`.head.iri())
    else
      super.resolve(params)

  private def isShapeParam(params: AmlCompletionRequest): Boolean =
    isScalarShape(params) && isInParams(params)

  private def isScalarShape(params: AmlCompletionRequest) =
    params.amfObject.meta.`type`.headOption.map(_.iri()).contains(ScalarShapeModel.`type`.head.iri())

  private def isInParams(params: AmlCompletionRequest) = {
    params.branchStack.headOption.exists(_.meta.`type`.headOption.exists(_.iri() == ParameterModel.`type`.head.iri()))
  }

  override protected def isHeader(params: AmlCompletionRequest): Boolean =
    params.yPartBranch.isKeyDescendanceOf("headers")

}
