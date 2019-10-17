package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml08

import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.RamlBooleanPropertyValue
import org.mulesoft.amfmanager.dialect.webapi.raml.raml08.Raml08TypesDialect

object Raml08BooleanPropertyValue extends RamlBooleanPropertyValue {
  override protected def propertyShapeNode: Option[NodeMapping] = Option(Raml08TypesDialect.PropertyShapeNode)
}