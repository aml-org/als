package org.mulesoft.amfintegration.dialect.dialects.jsonschema.base

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import amf.core.internal.metamodel.domain.ObjectNodeModel
import amf.shapes.internal.document.metamodel.JsonSchemaDocumentModel
import amf.shapes.internal.domain.metamodel.CreativeWorkModel
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.base.BaseJsonSchemaDocumentNode.jsonSchemaDocumentFacets

trait BaseJsonSchemaDocumentNode extends BaseAnyShapeNode {
  override def properties: Seq[PropertyMapping] = super.properties ++ jsonSchemaDocumentFacets(location)

  override def name = "BaseJsonSchemaDocumentNode"

  override def nodeTypeMapping: String = ObjectNodeModel.`type`.head.iri()
}

object BaseJsonSchemaDocumentNode {
  def jsonSchemaDocumentFacets(location: String): Seq[PropertyMapping] =
    Seq(
      // changing schema inside a node is currently not supported, so we will not suggest it
//      PropertyMapping()
//        .withId(location + "#/declarations/BaseJsonSchemaDocumentNode/schema")
//        .withNodePropertyMapping(JsonSchemaDocumentModel.SchemaVersion.value.iri())
//        .withName("$schema")
//        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/BaseJsonSchemaDocumentNode/title")
        .withNodePropertyMapping(CreativeWorkModel.Title.value.iri())
        .withName("title")
        .withLiteralRange(xsdString.iri())
    )
}
