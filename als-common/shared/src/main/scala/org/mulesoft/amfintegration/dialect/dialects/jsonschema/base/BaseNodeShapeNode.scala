package org.mulesoft.amfintegration.dialect.dialects.jsonschema.base

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdInteger, xsdString}
import amf.shapes.internal.domain.metamodel.{AnyShapeModel, NodeShapeModel}
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.base.BaseNodeShapeNode.nodeShapeFacets

trait BaseNodeShapeNode extends BaseAnyShapeNode {
  override def properties: Seq[PropertyMapping] = super.properties ++ nodeShapeFacets

  override def nodeTypeMapping: String = NodeShapeModel.`type`.head.iri()

  override def name = "NodeShape"
}

object BaseNodeShapeNode {
  def nodeShapeFacets(implicit location: String): Seq[PropertyMapping] =
    Seq(
      PropertyMapping()
        .withId(location + "#/declarations/NodeShapeNode/maxProperties")
        .withNodePropertyMapping(NodeShapeModel.MaxProperties.value.iri())
        .withName("maxProperties")
        withLiteralRange xsdInteger.iri(),
      PropertyMapping()
        .withId(location + "#/declarations/NodeShapeNode/minProperties")
        .withNodePropertyMapping(NodeShapeModel.MinProperties.value.iri())
        .withName("minProperties"),
      PropertyMapping()
        .withId(location + s"#declarations/NodeShapeNode/name")
        .withName("name")
        .withNodePropertyMapping(NodeShapeModel.Name.value.iri())
        .withLiteralRange(xsdString.iri())
        withLiteralRange xsdInteger.iri(),
      PropertyMapping()
        .withId(location + "#/declarations/NodeShapeNode/properties")
        .withNodePropertyMapping(NodeShapeModel.Properties.value.iri())
        .withName("properties")
        .withObjectRange(Seq(AnyShapeModel.`type`.head.iri()))
        .withMapTermKeyProperty(NodeShapeModel.Name.value.iri()),
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
        .withAllowMultiple(true)
    )
}
