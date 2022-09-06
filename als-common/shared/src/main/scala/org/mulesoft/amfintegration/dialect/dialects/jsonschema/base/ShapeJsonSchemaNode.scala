package org.mulesoft.amfintegration.dialect.dialects.jsonschema.base

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdAnyType
import amf.core.internal.metamodel.domain.ShapeModel

class ShapeJsonSchemaNode(dialectLocation: String, baseProps: String => Seq[PropertyMapping] = _ => Nil)
    extends BaseShapeNode {
  override def location: String                 = dialectLocation
  override def properties: Seq[PropertyMapping] = super.properties ++ baseProps(location)
}

class AnyShapeJsonSchemaNode(dialectLocation: String, baseProps: String => Seq[PropertyMapping] = _ => Nil)
    extends BaseAnyShapeNode {
  override def location: String                 = dialectLocation
  override def properties: Seq[PropertyMapping] = super.properties ++ baseProps(location)
}

class ArrayShapeJsonSchemaNode(dialectLocation: String, baseProps: String => Seq[PropertyMapping] = _ => Nil)
    extends BaseArrayShapeNode {
  override def location: String                 = dialectLocation
  override def properties: Seq[PropertyMapping] = super.properties ++ baseProps(location)
}

class NodeShapeJsonSchemaNode(dialectLocation: String, baseProps: String => Seq[PropertyMapping] = _ => Nil)
    extends BaseNodeShapeNode {
  override def location: String                 = dialectLocation
  override def properties: Seq[PropertyMapping] = super.properties ++ baseProps(location)
}

class NumberShapeJsonSchemaNode(dialectLocation: String, baseProps: String => Seq[PropertyMapping] = _ => Nil)
    extends BaseNumberShapeNode {
  override def location: String                 = dialectLocation
  override def properties: Seq[PropertyMapping] = super.properties ++ baseProps(location)
}

class StringShapeJsonSchemaNode(dialectLocation: String, baseProps: String => Seq[PropertyMapping] = _ => Nil)
    extends BaseStringShapeNode {
  override def location: String                 = dialectLocation
  override def properties: Seq[PropertyMapping] = super.properties ++ baseProps(location)
}
