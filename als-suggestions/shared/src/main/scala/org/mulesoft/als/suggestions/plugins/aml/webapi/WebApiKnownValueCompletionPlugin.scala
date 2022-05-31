package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.apicontract.client.scala.model.domain.Parameter
import amf.apicontract.internal.metamodel.domain.ParameterModel
import amf.core.internal.parser.domain.FieldEntry
import amf.shapes.internal.domain.metamodel.ScalarShapeModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.AbstractKnownValueCompletionPlugin
import org.mulesoft.amfintegration.AmfImplicits._

import scala.concurrent.Future

trait WebApiKnownValueCompletionPlugin extends AbstractKnownValueCompletionPlugin {

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    params.fieldEntry match {
      case None if isHeader(params) => innerResolver(params, ParameterModel.Name, ParameterModel.`type`.head.iri())
      case Some(fe) if isParamName(params, fe) =>
        if (isHeader(params)) innerResolver(params, ParameterModel.Name, ParameterModel.`type`.head.iri())
        else emptySuggestion
      case _ => super.resolve(params)
    }

  protected def isHeader(params: AmlCompletionRequest): Boolean =
    params.amfObject match {
      case p: Parameter if p.name.option().forall(_.isEmpty) =>
        p.binding.option().contains("header") || params.yPartBranch.parentEntryIs("headers")
      case _ => false
    }

  private def isParamName(params: AmlCompletionRequest, fe: FieldEntry): Boolean = isParam(params) && isName(fe)

  protected def isName(fe: FieldEntry): Boolean =
    fe.field.value.iri().equals(ParameterModel.Name.value.iri()) ||
      fe.field.value.iri().equals(ScalarShapeModel.DataType.value.iri())

  protected def isParam(params: AmlCompletionRequest): Boolean =
    params.amfObject.metaURIs.headOption.contains(ParameterModel.`type`.head.iri())

}

object WebApiKnownValueCompletionPlugin extends WebApiKnownValueCompletionPlugin
