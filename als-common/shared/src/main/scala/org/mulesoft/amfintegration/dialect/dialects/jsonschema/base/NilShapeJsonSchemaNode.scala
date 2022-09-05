package org.mulesoft.amfintegration.dialect.dialects.jsonschema.base

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.shapes.internal.domain.metamodel.NilShapeModel

class NilShapeJsonSchemaNode(dialectLocation: String, baseProps: String => Seq[PropertyMapping]) extends BaseShapeNode {
  override def location: String = dialectLocation

  override def name: String = "NilShape"

  override def properties: Seq[PropertyMapping] = super.properties ++ baseProps(dialectLocation)

  override def nodeTypeMapping: String = NilShapeModel.`type`.head.iri()
}
