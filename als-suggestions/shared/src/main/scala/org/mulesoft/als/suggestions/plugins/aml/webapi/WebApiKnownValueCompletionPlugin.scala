package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.core.parser.FieldEntry
import amf.plugins.domain.shapes.metamodel.ScalarShapeModel
import amf.plugins.domain.webapi.metamodel.ParameterModel
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.AbstractKnownValueCompletionPlugin
import org.mulesoft.amfmanager.AmfImplicits._

import scala.concurrent.Future

trait WebApiKnownValueCompletionPlugin extends AbstractKnownValueCompletionPlugin {

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    params.fieldEntry match {
      case Some(fe) if isParamName(params, fe) && !isHeader(params) => emptySuggestion
      case _                                                        => super.resolve(params)
    }

  protected def isHeader(params: AmlCompletionRequest): Boolean =
    params.amfObject match {
      case p: Parameter => p.binding.option().contains("header")
      case _            => false
    }

  private def isParamName(params: AmlCompletionRequest, fe: FieldEntry): Boolean = isParam(params) && isName(fe)

  protected def isName(fe: FieldEntry): Boolean =
    fe.field.value.iri().equals(ParameterModel.Name.value.iri()) ||
      fe.field.value.iri().equals(ScalarShapeModel.DataType.value.iri())

  protected def isParam(params: AmlCompletionRequest): Boolean =
    params.amfObject.metaURIs.headOption.contains(ParameterModel.`type`.head.iri())

}

object WebApiKnownValueCompletionPlugin extends WebApiKnownValueCompletionPlugin
