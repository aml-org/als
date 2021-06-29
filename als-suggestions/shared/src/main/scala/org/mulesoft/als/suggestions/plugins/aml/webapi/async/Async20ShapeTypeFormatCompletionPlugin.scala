package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.aml.client.scala.model.domain.NodeMapping
import amf.core.client.platform.model.DataTypes
import org.mulesoft.als.suggestions.plugins.aml.webapi.ShapeNumberShapeFormatValues
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema.{
  AnyShapeAsync2Node,
  NumberShapeAsync2Node,
  StringShapeAsync2Node
}

object Async20ShapeTypeFormatCompletionPlugin extends ShapeNumberShapeFormatValues {
  override def dataTypeNodeMapping(dataType: String): NodeMapping = dataType match {
    case DataTypes.Integer => NumberShapeAsync2Node.Obj
    case DataTypes.Number  => NumberShapeAsync2Node.Obj
    case DataTypes.String  => StringShapeAsync2Node.Obj
    case DataTypes.Boolean => AnyShapeAsync2Node.Obj
    case _                 => NumberShapeAsync2Node.Obj
  }

  override def id: String = "Async20ShapeTypeFormatCompletionPlugin"
}
