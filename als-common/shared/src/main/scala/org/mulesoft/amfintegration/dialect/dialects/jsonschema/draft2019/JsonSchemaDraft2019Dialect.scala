package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft2019

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.shapes.internal.spec.common.JSONSchemaDraft201909SchemaVersion
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.JsonSchemaBaseDialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.base.{
  BaseJsonSchemaDocumentNode,
  BaseNumberShapeNode,
  NumberShapeJsonSchemaNode
}
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.Draft7RootNode
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.JsonSchemaDraft7Dialect.{
  DialectLocation,
  baseProps
}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object JsonSchemaDraft2019Dialect extends JsonSchemaBaseDialect {

  override def DialectLocation: String = dialectLocation

  override protected val version: String = JSONSchemaDraft201909SchemaVersion.url

  override protected def encodes: DialectNode = Draft2019RootNode

  override protected def baseProps(location: String): Seq[PropertyMapping] =
    Draft7RootNode.conditionals(location) ++
      BaseJsonSchemaDocumentNode.jsonSchemaDocumentFacets(location) :+
      Draft2019RootNode.identifierMapping(location) :+
      Draft7RootNode.comment(location)

  override protected def numberNode: DialectNode = new NumberShapeJsonSchemaNode(
    DialectLocation,
    location => baseProps(location) ++ BaseNumberShapeNode.draft7Exclusives(location)
  )
}
