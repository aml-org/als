package org.mulesoft.amfintegration.dialect.dialects.jsonschema.base

import amf.aml.client.scala.model.domain.PropertyMapping
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.dialectLocation

trait BaseShapeJsonSchemaNode extends BaseShapeNode {
  override implicit def location: String = dialectLocation
}

class ShapeJsonSchemaNode(baseProps: String => Seq[PropertyMapping] = _ => Nil)
    extends BaseShapeNode
    with BaseShapeJsonSchemaNode {
  override def properties: Seq[PropertyMapping] = super.properties ++ baseProps(location)
}

class AnyShapeJsonSchemaNode(baseProps: String => Seq[PropertyMapping] = _ => Nil)
    extends BaseAnyShapeNode
    with BaseShapeJsonSchemaNode {
  override def properties: Seq[PropertyMapping] = super.properties ++ baseProps(location)
}

class ArrayShapeJsonSchemaNode(baseProps: String => Seq[PropertyMapping] = _ => Nil)
    extends BaseArrayShapeNode
    with BaseShapeJsonSchemaNode {
  override def properties: Seq[PropertyMapping] = super.properties ++ baseProps(location)
}

class NodeShapeJsonSchemaNode(baseProps: String => Seq[PropertyMapping] = _ => Nil)
    extends BaseNodeShapeNode
    with BaseShapeJsonSchemaNode {
  override def properties: Seq[PropertyMapping] = super.properties ++ baseProps(location)
}

class NumberShapeJsonSchemaNode(baseProps: String => Seq[PropertyMapping] = _ => Nil)
    extends BaseNumberShapeNode
    with BaseShapeJsonSchemaNode {
  override def properties: Seq[PropertyMapping] = super.properties ++ baseProps(location)
}

class StringShapeJsonSchemaNode(baseProps: String => Seq[PropertyMapping] = _ => Nil)
    extends BaseStringShapeNode
    with BaseShapeJsonSchemaNode {
  override def properties: Seq[PropertyMapping] = super.properties ++ baseProps(location)
}
