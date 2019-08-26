package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.core.model.domain.Shape
import amf.dialects.OAS20Dialect
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.ParameterModel
import amf.plugins.domain.webapi.models.{Parameter, Payload}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.{
  AMLEnumCompletionPlugin,
  AMLKnownValueCompletionPlugin,
  AMLKnownValueCompletions
}
import org.mulesoft.als.suggestions.plugins.aml.webapi.ShapeDeclarationReferenceCompletionPlugin

import scala.concurrent.{ExecutionContext, Future}

object OasTypeDeclarationReferenceCompletionPlugin extends ShapeDeclarationReferenceCompletionPlugin {
  override def id: String = "OasTypeDeclarationReferenceCompletionPlugin"

  override def typeProperty: PropertyMapping = OAS20Dialect.shapesPropertyMapping

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (params.amfObject.isInstanceOf[Shape] &&
        params.branchStack.headOption.exists(h => h.isInstanceOf[Parameter] || h.isInstanceOf[Payload])) {
      val key = params.yPartBranch.parentEntry.flatMap(_.key.asScalar.map(_.text)).getOrElse("")
      if (key == "in") resolveIn(params)
      else if (OAS20Dialect.DialectNodes.ParameterObject.propertiesMapping().exists(_.name().value() == key))
        emptySuggestion
      else if (params.yPartBranch.isValue && params.yPartBranch.parentEntryIs("schema")) emptySuggestion
      else super.resolve(params)
    } else emptySuggestion
  }

  private def resolveIn(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future {
      AMLEnumCompletionPlugin.getSuggestions(
        OAS20Dialect.DialectNodes.ParameterObject.propertiesMapping().find(_.name().value() == "in").toList,
        params.yPartBranch)
    }(ExecutionContext.Implicits.global)

}
