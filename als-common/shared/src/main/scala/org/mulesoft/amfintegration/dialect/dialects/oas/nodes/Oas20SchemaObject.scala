package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdString}
import amf.core.internal.metamodel.domain.ShapeModel
import amf.core.internal.metamodel.domain.extensions.PropertyShapeModel
import amf.shapes.internal.domain.metamodel.ArrayShapeModel
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.DialectLocation
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect

object Oas20SchemaObject extends AMLSchemaBaseObject {
  override def properties: Seq[PropertyMapping] =
    shapeOnly :+ PropertyMapping()
      .withId(DialectLocation + s"#/declarations/ShapeObject/collectionFormat")
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
}

object Oas30SchemaObject extends AMLSchemaBaseObject {
  override def properties: Seq[PropertyMapping] = shapeOnly ++ Seq(
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/SchemaObject/allOf")
      .withName("allOf")
      .withNodePropertyMapping(ShapeModel.And.value.iri())
      .withObjectRange(Seq(Oas30SchemaObject.id)),
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
      .withId(OAS30Dialect.DialectLocation + "#/declarations/SchemaObject/items")
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
}

trait AMLSchemaBaseObject extends DialectNode with AMLShapeBaseProperties {
  override def name: String            = "schema"
  override def nodeTypeMapping: String = ShapeModel.`type`.head.iri()

}

object AMLSchemaBaseObject extends AMLSchemaBaseObject {
  override def properties: Seq[PropertyMapping] = shapeOnly
}
