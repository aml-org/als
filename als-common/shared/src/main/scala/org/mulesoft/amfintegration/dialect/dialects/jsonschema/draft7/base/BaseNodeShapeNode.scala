package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.base

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdInteger, xsdString}
import amf.core.internal.metamodel.domain.extensions.PropertyShapeModel
import amf.shapes.internal.domain.metamodel.NodeShapeModel

trait BaseNodeShapeNode extends BaseAnyShapeNode {
  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + "#/declarations/NodeShapeNode/maxProperties")
      .withNodePropertyMapping(NodeShapeModel.MaxProperties.value.iri())
      .withName("maxProperties")
      withLiteralRange xsdInteger.iri(),
    PropertyMapping()
      .withId(location + "#/declarations/NodeShapeNode/minProperties")
      .withNodePropertyMapping(NodeShapeModel.MinProperties.value.iri())
      .withName("minProperties")
      withLiteralRange xsdInteger.iri(),
    PropertyMapping()
      .withId(location + "#/declarations/NodeShapeNode/properties")
      .withNodePropertyMapping(NodeShapeModel.Properties.value.iri())
      .withName("properties")
      .withObjectRange(Seq("ShapeObjectId"))
      .withMapTermKeyProperty(PropertyShapeModel.Name.value.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/ShapeObject/patternProperties")
      .withName("patternProperties")
      .withNodePropertyMapping(NodeShapeModel.Properties.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/NodeShapeNode/additionalProperties")
      .withNodePropertyMapping(NodeShapeModel.Closed.value.iri())
      .withName("additionalProperties")
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#declarations/SchemaObject/dependencies")
      .withName("dependencies")
      .withNodePropertyMapping(NodeShapeModel.Dependencies.value.iri())
      .withLiteralRange(xsdString.iri())
      .withAllowMultiple(true),
    PropertyMapping()
      .withId(location + s"#/declarations/SchemaObject/propertyNames")
      .withName("propertyNames")
      .withNodePropertyMapping(NodeShapeModel.PropertyNames.value.iri())
      .withObjectRange(Seq("ShapeObjectId"))
  )

  override def nodeTypeMapping: String = NodeShapeModel.`type`.head.iri()

  override def name = "NodeShape"
}
