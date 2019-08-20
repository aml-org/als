package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.dialects.OAS20Dialect
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.suggestions.plugins.aml.webapi.ShapeDeclarationReferenceCompletionPlugin

object OasTypeDeclarationReferenceCompletionPlugin extends ShapeDeclarationReferenceCompletionPlugin {
  override def id: String = "OasTypeDeclarationReferenceCompletionPlugin"

  override def typeProperty: PropertyMapping = OAS20Dialect.shapesPropertyMapping
}
