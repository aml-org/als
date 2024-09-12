package org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.NodeMapping
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.Async20TypeFacetsCompletionPlugin.dialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema.StringShapeAsync2Node
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroFieldNode.PropertyShapeAvroNode
import org.mulesoft.amfintegration.dialect.dialects.avro.{AvroAnyNode, AvroDialect, AvroPrimitiveNode, AvroTypesDialect}

object AvroTypeFacetsCompletionPlugin extends WebApiTypeFacetsCompletionPlugin {

  override def id: String = "AvroTypeFacetsCompletionPlugin"

  val dialect: Dialect = AvroDialect.dialect

//TODO CHECK HOW TO COMPLETE THIS
  override def stringShapeNode: NodeMapping = AvroPrimitiveNode.Obj

  override def numberShapeNode: NodeMapping = AvroPrimitiveNode.Obj

  override def integerShapeNode: NodeMapping = AvroPrimitiveNode.Obj

  override def anyShapeNode: NodeMapping = AvroAnyNode.Obj

  override def declarations: Seq[NodeMapping] = dialect.declares.collect({ case n: NodeMapping => n })

  override def propertyShapeNode: Option[NodeMapping] = None
}
