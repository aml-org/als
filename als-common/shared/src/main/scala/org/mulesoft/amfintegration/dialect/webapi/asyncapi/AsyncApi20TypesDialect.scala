package org.mulesoft.amfintegration.dialect.webapi.asyncapi

import amf.core.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdString}
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{DocumentMapping, NodeMapping, PropertyMapping, PublicNodeMapping}
import amf.plugins.domain.shapes.metamodel._
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.webapi.JsonSchemaDraft7TypesDialect

object AsyncApi20TypesDialect {
  val DialectLocation: String = AsyncApi20Dialect.DialectLocation
  val ShapeNodeId: String = DialectLocation + "#/declarations/ShapeNode"
  val AnyShapeNode: NodeMapping = JsonSchemaDraft7TypesDialect.AnyShapeNode
                    .withPropertiesMapping(
                      JsonSchemaDraft7TypesDialect.anyShapeProperties ++ Seq(
                        PropertyMapping()
                          .withId(DialectLocation + "#/declarations/AnyShapeNode/format")
                          .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
                          .withName("format")
                          .withEnum(
                            Seq(
                              "int32",
                              "int64",
                              "float",
                              "double",
                              "byte",
                              "binary",
                              "date",
                              "date-time",
                              "password"
                            ))
                          .withLiteralRange(xsdString.iri()),
                        PropertyMapping()
                          .withId(DialectLocation + "#/declarations/AnyShapeNode/discriminator")
                          .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
                          .withName("discriminator")
                          .withLiteralRange(xsdString.iri()),
                        PropertyMapping()
                          .withId(DialectLocation + "#/declarations/AnyShapeNode/deprecated")
                          .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
                          .withName("deprecated")
                          .withLiteralRange(xsdBoolean.iri()),
                        PropertyMapping()
                          .withId(DialectLocation + "#/declarations/AnyShapeNode/externalDocs")
                          .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
                          .withName("externalDocs")
                          .withLiteralRange(AnyShapeNode.id)
                      ))
  val NodeShapeNode: NodeMapping = JsonSchemaDraft7TypesDialect.NodeShapeNode
  val ArrayShapeNode: NodeMapping = JsonSchemaDraft7TypesDialect.ArrayShapeNode
  val StringShapeNode: NodeMapping = JsonSchemaDraft7TypesDialect.StringShapeNode
  val NumberShapeNode: NodeMapping = JsonSchemaDraft7TypesDialect.NumberShapeNode
  val NilShapeNode: NodeMapping = JsonSchemaDraft7TypesDialect.NilShapeNode

  val dialect: Dialect = {
    val dialect = AsyncApi20Dialect()
    dialect.withDeclares(
      dialect.declares ++
        Seq(
          AnyShapeNode,
          NodeShapeNode,
          ArrayShapeNode,
          StringShapeNode,
          NumberShapeNode,
          NilShapeNode
        ))

    val declaredNodes = Seq(
      PublicNodeMapping()
        .withId(DialectLocation + "#/documents/types")
        .withName("types")
        .withMappedNode(ShapeNodeId),
    )

    dialect.documents().root().withDeclaredNodes(declaredNodes)

    dialect
      .documents()
      .withLibrary(
        DocumentMapping()
          .withId(DialectLocation + "#/library")
          .withDeclaredNodes(declaredNodes))
    dialect
  }
}

