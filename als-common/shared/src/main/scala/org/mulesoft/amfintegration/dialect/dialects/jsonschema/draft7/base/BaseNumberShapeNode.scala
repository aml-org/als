package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.base

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdFloat, xsdInteger}
import amf.shapes.internal.domain.metamodel.ScalarShapeModel

trait BaseNumberShapeNode extends BaseAnyShapeNode {


  override def nodeTypeMapping: String = ScalarShapeModel.`type`.head.iri()

  override def name: String = "NumberShape"

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
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
      .withId(location + s"#/declarations/ShapeObject/exclusiveMaximum")
      .withName("exclusiveMaximum")
      .withNodePropertyMapping(ScalarShapeModel.ExclusiveMaximum.value.iri())
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/ShapeObject/exclusiveMinimum")
      .withName("exclusiveMinimum")
      .withNodePropertyMapping(ScalarShapeModel.ExclusiveMinimum.value.iri())
      .withLiteralRange(xsdInteger.iri()),
  )
}
