package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7

import amf.shapes.internal.domain.metamodel.NilShapeModel

object NilShapeDraft7Node extends BaseShapeDraft7Node {
  override def name: String = "NilShape"

  override def nodeTypeMapping: String = NilShapeModel.`type`.head.iri()
}
