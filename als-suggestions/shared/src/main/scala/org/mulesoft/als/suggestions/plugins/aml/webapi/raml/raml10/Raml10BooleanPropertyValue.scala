package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10

import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.RamlBooleanPropertyValue

object Raml10BooleanPropertyValue extends RamlBooleanPropertyValue {
  override protected def propertyShapeNode: Option[NodeMapping] = Option(Raml10TypesDialect.PropertyShapeNode)
}