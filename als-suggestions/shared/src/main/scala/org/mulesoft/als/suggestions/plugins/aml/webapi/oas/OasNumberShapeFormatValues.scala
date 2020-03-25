package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.client.model.DataTypes
import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.suggestions.plugins.aml.webapi.ShapeNumberShapeFormatValues
import org.mulesoft.amfmanager.dialect.webapi.oas.Oas20DialectWrapper

object OasNumberShapeFormatValues extends ShapeNumberShapeFormatValues {
  override def id: String = "OasNumberShapeFormatValues"

  override def dataTypeNodeMapping(dataType: String): NodeMapping = dataType match {
    case DataTypes.Integer => Oas20DialectWrapper.JsonSchemas.IntegerSchemaObject
    case DataTypes.String  => Oas20DialectWrapper.JsonSchemas.StringSchemaObject
    case DataTypes.Boolean => Oas20DialectWrapper.JsonSchemas.AnySchemaObject
    case _                 => Oas20DialectWrapper.JsonSchemas.NumberSchemaObject
  }
}
