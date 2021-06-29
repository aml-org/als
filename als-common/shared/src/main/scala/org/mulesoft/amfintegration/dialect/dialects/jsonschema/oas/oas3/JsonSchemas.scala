package org.mulesoft.amfintegration.dialect.dialects.jsonschema.oas.oas3

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdBoolean
import amf.core.internal.metamodel.domain.ShapeModel
import amf.core.internal.metamodel.domain.extensions.PropertyShapeModel
import amf.shapes.internal.domain.metamodel.NodeShapeModel
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.JsonSchemaForOasWrapper
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.DialectLocation
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.{AMLDiscriminatorObject, Oas30SchemaObject}

object JsonSchemas extends JsonSchemaForOasWrapper {

  override def specProperties: Seq[PropertyMapping] =
    Seq(
      PropertyMapping()
        .withId(OAS30Dialect.DialectLocation + "#/declarations/SchemaObject/oneOf")
        .withName("oneOf")
        .withNodePropertyMapping(ShapeModel.Xone.value.iri())
        .withObjectRange(Seq(Oas30SchemaObject.id)),
      PropertyMapping()
        .withId(OAS30Dialect.DialectLocation + "#/declarations/SchemaObject/anyOf")
        .withName("anyOf")
        .withNodePropertyMapping(ShapeModel.Or.value.iri())
        .withObjectRange(Seq(Oas30SchemaObject.id))
        .withAllowMultiple(true),
      PropertyMapping()
        .withId(OAS30Dialect.DialectLocation + "#/declarations/SchemaObject/not")
        .withName("not")
        .withNodePropertyMapping(ShapeModel.Not.value.iri())
        .withObjectRange(Seq(Oas30SchemaObject.id)),
      PropertyMapping()
        .withId(OAS30Dialect.DialectLocation + "#/declarations/SchemaObject/nullable")
        .withName("nullable")
        .withNodePropertyMapping("id://NullableId/nullable")
        .withLiteralRange(xsdBoolean.iri()),
      PropertyMapping()
        .withId(OAS30Dialect.DialectLocation + "#/declarations/SchemaObject/writeOnly")
        .withName("writeOnly")
        .withNodePropertyMapping(PropertyShapeModel.WriteOnly.value.iri())
        .withLiteralRange(xsdBoolean.iri()),
      PropertyMapping()
        .withId(OAS30Dialect.DialectLocation + "#/declarations/SchemaObject/deprecated")
        .withName("deprecated")
        .withNodePropertyMapping(PropertyShapeModel.Deprecated.value.iri())
        .withLiteralRange(xsdBoolean.iri())
    )

  override def arraySpecProperties: Seq[PropertyMapping] = Nil

  override def specNodeProperties: Seq[PropertyMapping] = Nil

  val discriminatorProperty: PropertyMapping = PropertyMapping()
    .withId(DialectLocation + s"#/declarations/Schema/discriminator")
    .withName("discriminator")
    .withNodePropertyMapping(NodeShapeModel.Discriminator.value.iri())
    .withObjectRange(Seq(AMLDiscriminatorObject.id))
    .withMapTermKeyProperty(NodeShapeModel.Discriminator.value.iri())
}
