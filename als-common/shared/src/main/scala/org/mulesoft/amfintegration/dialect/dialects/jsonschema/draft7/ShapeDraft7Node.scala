package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7

import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.base.{
  BaseAnyShapeNode,
  BaseArrayShapeNode,
  BaseNodeShapeNode,
  BaseNumberShapeNode,
  BaseShapeNode,
  BaseStringShapeNode
}

trait BaseShapeDraft7Node extends BaseShapeNode {
  override def location: String = dialectLocation
}

object ShapeDraft7Node extends BaseShapeNode with BaseShapeDraft7Node

object AnyShapeDraft7Node extends BaseAnyShapeNode with BaseShapeDraft7Node

object ArrayShapeDraft7Node extends BaseArrayShapeNode with BaseShapeDraft7Node

object NodeShapeDraft7Node extends BaseNodeShapeNode with BaseShapeDraft7Node

object NumberShapeDraft7Node extends BaseNumberShapeNode with BaseShapeDraft7Node

object StringShapeDraft7Node extends BaseStringShapeNode with BaseShapeDraft7Node
