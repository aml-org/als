package org.mulesoft.amfintegration.dialect.dialects.jsonschema.oas.oas2

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import amf.shapes.internal.domain.metamodel.{ArrayShapeModel, NodeShapeModel}
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.JsonSchemaForOasWrapper
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.DialectLocation

object JsonSchemas extends JsonSchemaForOasWrapper {
  override def arraySpecProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/Schema/collectionFormat")
      .withName("collectionFormat")
      .withNodePropertyMapping(ArrayShapeModel.CollectionFormat.value.iri())
      .withEnum(
        Seq(
          "csv",
          "ssv",
          "tsv",
          "pipes",
          "multi"
        ))
      .withLiteralRange(xsdString.iri())
  )

  override def specNodeProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + s"#/declarations/Schema/discriminator")
      .withName("discriminator")
      .withNodePropertyMapping(NodeShapeModel.Discriminator.value.iri())
      .withLiteralRange(xsdString.iri())
  )

  override def specProperties: Seq[PropertyMapping] = Nil
}
