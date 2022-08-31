package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.shapes.internal.spec.common.JSONSchemaDraft7SchemaVersion
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.JsonSchemaBaseDialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.base.BaseJsonSchemaDocumentNode
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft4.Draft4RootNode
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object JsonSchemaDraft7Dialect extends JsonSchemaBaseDialect {

  override val DialectLocation: String = dialectLocation

  override protected val version: String = JSONSchemaDraft7SchemaVersion.url

  override protected def encodes: DialectNode = Draft7RootNode

  override protected def baseProps(location: String): Seq[PropertyMapping] =
    BaseJsonSchemaDocumentNode.jsonSchemaDocumentFacets(location) ++
      Draft7RootNode.conditionals(location) :+
      Draft7RootNode.identifierMapping(location) :+
      Draft7RootNode.comment(location)
}
