package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.suggestions.plugins.aml.webapi.ShapeDeclarationReferenceCompletionPlugin
import org.mulesoft.amfmanager.dialect.webapi.raml.raml10.Raml10TypesDialect

object RamlTypeDeclarationReferenceCompletionPlugin extends ShapeDeclarationReferenceCompletionPlugin {
  override def id: String = "RamlTypeDeclarationReferenceCompletionPlugin"

  override def typeProperty: PropertyMapping =
    Raml10TypesDialect.shapeTypesProperty
}
