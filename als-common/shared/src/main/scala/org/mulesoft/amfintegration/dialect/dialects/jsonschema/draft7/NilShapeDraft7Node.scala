package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7

import amf.plugins.domain.shapes.metamodel.NilShapeModel
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.base.BaseShapeNode

object NilShapeDraft7Node extends BaseShapeNode {
  override def name: String = "NilShape"

  override def nodeTypeMapping: String = NilShapeModel.`type`.head.iri()
}
