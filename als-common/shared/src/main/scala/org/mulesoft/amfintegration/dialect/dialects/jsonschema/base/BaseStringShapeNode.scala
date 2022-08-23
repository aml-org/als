package org.mulesoft.amfintegration.dialect.dialects.jsonschema.base

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdInteger, xsdString}
import amf.shapes.internal.domain.metamodel.ScalarShapeModel
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema.StringShapeAsync2Node.location
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.base.BaseStringShapeNode.stringShapeFacets

trait BaseStringShapeNode extends BaseAnyShapeNode {
  override def properties: Seq[PropertyMapping] = super.properties ++ stringShapeFacets

  override def name = "StringShape"

  override def nodeTypeMapping: String = ScalarShapeModel.`type`.head.iri()
}

object BaseStringShapeNode {
  def stringShapeFacets(implicit location: String): Seq[PropertyMapping] =
    Seq(
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
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/ScalarShapeNode/format")
        .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
        .withName("format")
        .withEnum(
          Seq(
            "byte",
            "binary",
            "date",
            "date-time",
            "password"
          )
        )
        .withLiteralRange(xsdString.iri())
    )
}
