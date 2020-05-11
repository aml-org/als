package org.mulesoft.amfmanager.dialect.webapi.oas

import amf.core.metamodel.domain.ShapeModel
import amf.core.metamodel.domain.extensions.PropertyShapeModel
import amf.core.vocabulary.Namespace.XsdTypes._
import amf.dialects.OAS30Dialect
import amf.dialects.OAS30Dialect.DialectLocation
import amf.dialects.oas.nodes._
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{PropertyMapping, PublicNodeMapping}
import amf.plugins.domain.shapes.metamodel.NodeShapeModel

object Oas30DialectWrapper {
  def apply(): Dialect = dialect

  // hack to force object initialization in amf and avoid exception
  private val orignalId = OAS30Dialect().id

  lazy val dialect: Dialect = {

    val d = OAS30Dialect()
    d.withDeclares(
      d.declares.filter(p => !(p.id == Oas30SchemaObject.id)) ++ Seq(
        JsonSchemas.SchemaObject,
        JsonSchemas.AnySchemaObject,
        JsonSchemas.ArraySchemaObject,
        JsonSchemas.IntegerSchemaObject,
        JsonSchemas.NodeShapeObject,
        JsonSchemas.NumberSchemaObject,
        JsonSchemas.StringSchemaObject
      ))
  }

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
          .withObjectRange(Seq(Oas30SchemaObject.id)),
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
}
