package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml08

import amf.apicontract.internal.metamodel.domain.ParameterModel
import amf.shapes.internal.domain.metamodel.ScalarShapeModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiKnownValueCompletionPlugin
import org.mulesoft.amfintegration.AmfImplicits._

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
    params.amfObject.metaURIs.headOption.contains(ScalarShapeModel.`type`.head.iri())

  private def isInParams(params: AmlCompletionRequest) = {
    params.branchStack.headOption.exists(_.metaURIs.headOption.exists(_ == ParameterModel.`type`.head.iri()))
  }

  override protected def isHeader(params: AmlCompletionRequest): Boolean =
    params.astPartBranch.isKeyDescendantOf("headers")

}
