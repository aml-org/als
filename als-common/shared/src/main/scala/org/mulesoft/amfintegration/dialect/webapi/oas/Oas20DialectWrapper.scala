package org.mulesoft.amfmanager.dialect.webapi.oas

import amf.core.vocabulary.Namespace.XsdTypes._
import amf.dialects.OAS20Dialect
import amf.dialects.OAS20Dialect.DialectLocation
import amf.dialects.oas.nodes.Oas20SchemaObject
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import amf.plugins.domain.shapes.metamodel.{ArrayShapeModel, NodeShapeModel}
import amf.plugins.domain.webapi.metamodel.PayloadModel

object Oas20DialectWrapper {

  // hack to force object initialization in amf and avoid exception
  private val orignalId = OAS20Dialect().id

  private val PayloadParameter = NodeMapping()
    .withId("#/declarations/PayloadParameter")
    .withName("PayloadPArameter")
    .withNodeTypeMapping(PayloadModel.`type`.head.iri())

  lazy val dialect: Dialect = {

    val d = OAS20Dialect()
    d.withDeclares(
      d.declares.filter(p => !(p.id == Oas20SchemaObject.id)) ++ Seq(
        JsonSchemas.SchemaObject,
        JsonSchemas.AnySchemaObject,
        JsonSchemas.ArraySchemaObject,
        JsonSchemas.IntegerSchemaObject,
        JsonSchemas.NodeShapeObject,
        JsonSchemas.NumberSchemaObject,
        JsonSchemas.StringSchemaObject,
      ))
  }
  // shapes schema
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
}
