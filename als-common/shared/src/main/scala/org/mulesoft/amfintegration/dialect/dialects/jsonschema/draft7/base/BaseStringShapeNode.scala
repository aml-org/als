package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.base

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdInteger, xsdString}
import amf.shapes.internal.domain.metamodel.ScalarShapeModel

trait BaseStringShapeNode extends BaseAnyShapeNode {
  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + "#/declarations/ScalarShapeNode/pattern")
      .withNodePropertyMapping(ScalarShapeModel.Pattern.value.iri())
      .withName("pattern")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/ScalarShapeNode/minLength")
      .withNodePropertyMapping(ScalarShapeModel.MinLength.value.iri())
      .withName("minLength")
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/ScalarShapeNode/maxLength")
      .withNodePropertyMapping(ScalarShapeModel.MaxLength.value.iri())
      .withName("maxLength")
      .withLiteralRange(xsdInteger.iri())
  )

  override def name = "StringShape"

  override def nodeTypeMapping = ScalarShapeModel.`type`.head.iri()
}
