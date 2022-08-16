package org.mulesoft.amfintegration.dialect.dialects.jsonschema.base

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.shapes.internal.domain.metamodel.NilShapeModel

class NilShapeJsonSchemaNode(baseProps: String => Seq[PropertyMapping]) extends BaseShapeJsonSchemaNode {
  override def name: String = "NilShape"

  override def properties: Seq[PropertyMapping] = super.properties ++ baseProps(location)

  override def nodeTypeMapping: String = NilShapeModel.`type`.head.iri()
}
