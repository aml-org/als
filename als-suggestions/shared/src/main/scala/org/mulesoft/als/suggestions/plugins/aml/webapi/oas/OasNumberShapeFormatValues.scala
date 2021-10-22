package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.aml.client.scala.model.domain.NodeMapping
import amf.core.client.platform.model.DataTypes
import org.mulesoft.als.suggestions.plugins.aml.webapi.ShapeNumberShapeFormatValues
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.oas.oas2.JsonSchemas

object OasNumberShapeFormatValues extends ShapeNumberShapeFormatValues {
  override def id: String = "OasNumberShapeFormatValues"

  override def dataTypeNodeMapping(dataType: String): NodeMapping = dataType match {
    case DataTypes.Integer => JsonSchemas.IntegerSchemaObject
    case DataTypes.String  => JsonSchemas.StringSchemaObject
    case DataTypes.Boolean => JsonSchemas.AnySchemaObject
    case _                 => JsonSchemas.NumberSchemaObject
  }
}
