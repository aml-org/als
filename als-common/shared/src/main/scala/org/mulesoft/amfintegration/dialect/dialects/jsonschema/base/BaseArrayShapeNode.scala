package org.mulesoft.amfintegration.dialect.dialects.jsonschema.base

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdInteger}
import amf.shapes.internal.domain.metamodel.{ArrayShapeModel, TupleShapeModel}
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.base.BaseArrayShapeNode.arrayShapeFacets

trait BaseArrayShapeNode extends BaseAnyShapeNode {
  override def properties: Seq[PropertyMapping] = super.properties ++ arrayShapeFacets(location)

  override def name: String            = "ArrayShape"
  override def nodeTypeMapping: String = ArrayShapeModel.`type`.head.iri()
}

object BaseArrayShapeNode {
  def arrayShapeFacets(location: String): Seq[PropertyMapping] =
    Seq(
      PropertyMapping()
        .withId(location + "#/declarations/ArrayShapeNode/items")
        .withNodePropertyMapping(ArrayShapeModel.Items.value.iri())
        .withName("items")
        .withObjectRange(Seq("ShapeNodeId")),
      PropertyMapping()
        .withId(location + "#/declarations/ArrayShapeNode/minItems")
        .withNodePropertyMapping(ArrayShapeModel.MinItems.value.iri())
        .withName("minItems")
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/ArrayShapeNode/maxItems")
        .withNodePropertyMapping(ArrayShapeModel.MaxItems.value.iri())
        .withName("maxItems")
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/ArrayShapeNode/uniqueItems")
        .withNodePropertyMapping(ArrayShapeModel.UniqueItems.value.iri())
        .withName("uniqueItems")
        .withLiteralRange(xsdBoolean.iri()),
      PropertyMapping()
        .withId(location + s"#/declarations/SchemaObject/additionalItems")
        .withName("additionalItems")
        .withNodePropertyMapping(TupleShapeModel.AdditionalItemsSchema.value.iri())
        .withObjectRange(Seq("ShapeObjectId")),
      PropertyMapping()
        .withId(location + s"#/declarations/SchemaObject/contains")
        .withName("contains")
        .withNodePropertyMapping(TupleShapeModel.Contains.value.iri())
        .withObjectRange(Seq("ShapeObjectId"))
    )
}
