package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.aml.client.scala.model.domain.NodeMapping
import org.mulesoft.als.suggestions.plugins.aml.webapi.ShapeNumberShapeFormatValues
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

object RamlNumberShapeFormatValues extends ShapeNumberShapeFormatValues {
  override def id: String = "RamlNumberShapeFormatValues"

  override def dataTypeNodeMapping(dataType: String): NodeMapping =
    Raml10TypesDialect.NumberShapeNode
}
