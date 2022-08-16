package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft4

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.shapes.internal.spec.common.JSONSchemaDraft4SchemaVersion
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.JsonSchemaBaseDialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.base.BaseJsonSchemaDocumentNode
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object JsonSchemaDraft4Dialect extends JsonSchemaBaseDialect {

  override val DialectLocation: String = dialectLocation

  override protected val version: String = JSONSchemaDraft4SchemaVersion.url

  override protected def encodes: DialectNode = Draft4RootNode

  override protected def baseProps(location: String): Seq[PropertyMapping] =
    Draft4RootNode.identifierMapping(location) +:
      BaseJsonSchemaDocumentNode.jsonSchemaDocumentFacets(location)
}
