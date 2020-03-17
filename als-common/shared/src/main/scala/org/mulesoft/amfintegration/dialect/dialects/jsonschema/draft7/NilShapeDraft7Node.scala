package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7

import amf.plugins.domain.shapes.metamodel.NilShapeModel

object NilShapeDraft7Node extends BaseShapeDraft7Node {
  override def name: String = "NilShape"

  override def nodeTypeMapping: String = NilShapeModel.`type`.head.iri()
}
