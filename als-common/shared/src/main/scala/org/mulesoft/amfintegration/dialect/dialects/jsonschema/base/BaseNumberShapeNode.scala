package org.mulesoft.amfintegration.dialect.dialects.jsonschema.base

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdFloat, xsdString}
import amf.shapes.internal.domain.metamodel.ScalarShapeModel
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.base.BaseNumberShapeNode.numberShapeFacets

trait BaseNumberShapeNode extends BaseAnyShapeNode {

  override def nodeTypeMapping: String = ScalarShapeModel.`type`.head.iri()

  override def name: String = "NumberShape"

  override def properties: Seq[PropertyMapping] = super.properties ++ numberShapeFacets(location)
}

object BaseNumberShapeNode {
  def draft4Exclusives(location: String): Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/ShapeObject/exclusiveMaximum")
      .withName("exclusiveMaximum")
      .withNodePropertyMapping(ScalarShapeModel.ExclusiveMaximum.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/ShapeObject/exclusiveMinimum")
      .withName("exclusiveMinimum")
      .withNodePropertyMapping(ScalarShapeModel.ExclusiveMinimum.value.iri())
      .withLiteralRange(xsdBoolean.iri())
  )

  def draft7Exclusives(location: String): Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/ShapeObject/exclusiveMaximum")
      .withName("exclusiveMaximum")
      .withNodePropertyMapping(ScalarShapeModel.ExclusiveMaximum.value.iri())
      .withLiteralRange(xsdFloat.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/ShapeObject/exclusiveMinimum")
      .withName("exclusiveMinimum")
      .withNodePropertyMapping(ScalarShapeModel.ExclusiveMinimum.value.iri())
      .withLiteralRange(xsdFloat.iri())
  )

  def numberShapeFacets(location: String): Seq[PropertyMapping] =
    Seq(
      PropertyMapping()
        .withId(location + "#/declarations/ScalarShapeNode/minimum")
        .withNodePropertyMapping(ScalarShapeModel.Minimum.value.iri())
        .withName("minimum")
        .withLiteralRange(xsdFloat.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/ScalarShapeNode/maximun")
        .withNodePropertyMapping(ScalarShapeModel.Maximum.value.iri())
        .withName("maximum")
        .withLiteralRange(xsdFloat.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/ScalarShapeNode/multipleOf")
        .withNodePropertyMapping(ScalarShapeModel.MultipleOf.value.iri())
        .withName("multipleOf")
        .withLiteralRange(xsdFloat.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/ShapeObject/format")
        .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
        .withName("format")
        .withLiteralRange(xsdString.iri())
    )
}
