package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10

import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.RamlBooleanPropertyValue
import org.mulesoft.amfmanager.dialect.webapi.raml.raml10.Raml10TypesDialect

object Raml10BooleanPropertyValue extends RamlBooleanPropertyValue {
  override protected def propertyShapeNode: Option[NodeMapping] = Option(Raml10TypesDialect.PropertyShapeNode)
}