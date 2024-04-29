package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.aml.client.scala.model.domain.PropertyMapping
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.ShapeDeclarationReferenceCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

import scala.concurrent.Future

object RamlTypeDeclarationReferenceCompletionPlugin extends ShapeDeclarationReferenceCompletionPlugin {
  override def id: String = "RamlTypeDeclarationReferenceCompletionPlugin"

  override def typeProperty: PropertyMapping =
    Raml10TypesDialect.shapeTypesProperty
}

object Raml08TypeDeclarationReferenceCompletionPlugin extends ShapeDeclarationReferenceCompletionPlugin {
  override def id: String = "RamlTypeDeclarationReferenceCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = emptySuggestion

  override def typeProperty: PropertyMapping =
    Raml10TypesDialect.shapeTypesProperty
}
