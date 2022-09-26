package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.shapes.internal.spec.common.JSONSchemaDraft7SchemaVersion
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.JsonSchemaBaseDialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.base.{
  BaseJsonSchemaDocumentNode,
  BaseNumberShapeNode,
  NumberShapeJsonSchemaNode
}
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft4.JsonSchemaDraft4Dialect.{
  DialectLocation,
  baseProps
}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object JsonSchemaDraft7Dialect extends JsonSchemaBaseDialect {

  override def DialectLocation: String = dialectLocation

  override protected val version: String = JSONSchemaDraft7SchemaVersion.url

  override protected def encodes: DialectNode = Draft7RootNode

  override protected def baseProps(location: String): Seq[PropertyMapping] =
    BaseJsonSchemaDocumentNode.jsonSchemaDocumentFacets(location) ++
      Draft7RootNode.conditionals(location) :+
      Draft7RootNode.identifierMapping(location) :+
      Draft7RootNode.comment(location)

  override protected def numberNode: DialectNode = new NumberShapeJsonSchemaNode(
    DialectLocation,
    location => baseProps(location) ++ BaseNumberShapeNode.draft7Exclusives(location)
  )
}
