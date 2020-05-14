package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.client.model.DataTypes
import amf.plugins.document.vocabularies.model.domain.NodeMapping
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
