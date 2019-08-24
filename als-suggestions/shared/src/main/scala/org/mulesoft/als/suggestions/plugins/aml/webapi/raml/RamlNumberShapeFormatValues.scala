package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.suggestions.plugins.aml.webapi.ShapeNumberShapeFormatValues

object RamlNumberShapeFormatValues extends ShapeNumberShapeFormatValues {
  override def id: String = "RamlNumberShapeFormatValues"

  override def numberNodeMapping(dataType: String): NodeMapping = Raml10TypesDialect.NumberShapeNode
}
