package org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.NodeMapping
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema.StringShapeAsync2Node
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroFieldNode.PropertyShapeAvroNode
import org.mulesoft.amfintegration.dialect.dialects.avro.{AvroDialect, AvroTypesDialect}

object AvroTypeFacetsCompletionPlugin extends WebApiTypeFacetsCompletionPlugin {

  override def id: String = "AvroTypeFacetsCompletionPlugin"

  val dialect: Dialect = AvroDialect.dialect

//TODO CHECK HOW TO COMPLETE THIS
  override def stringShapeNode: NodeMapping = AvroTypesDialect.StringShapeNode

  override def numberShapeNode: NodeMapping = StringShapeAsync2Node.Obj

  override def integerShapeNode: NodeMapping = StringShapeAsync2Node.Obj

  override def anyShapeNode: NodeMapping = ???

  override def declarations: Seq[NodeMapping] = ???

  override def propertyShapeNode: Option[NodeMapping] = PropertyShapeAvroNode
}
