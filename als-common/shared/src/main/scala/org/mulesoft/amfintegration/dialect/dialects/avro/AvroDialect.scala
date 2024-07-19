package org.mulesoft.amfintegration.dialect.dialects.avro

import amf.aml.client.scala.model.domain.{DocumentsModel, PropertyMapping}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import amf.core.internal.metamodel.domain.ShapeModel
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import amf.shapes.internal.document.metamodel.AvroSchemaDocumentModel
import amf.shapes.internal.domain.metamodel.AnyShapeModel
import org.mulesoft.amfintegration.dialect.BaseDialect
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object AvroDialect extends BaseDialect {
  override def DialectLocation: String = "file://vocabularies/dialects/avro.yaml"

  override protected val name: String    = "avro"
  override protected val version: String = ""

  override protected def emptyDocument: DocumentsModel =
    DocumentsModel()
      .withId(DialectLocation + "#/documents")
      .withReferenceStyle(ReferenceStyles.JSONSCHEMA)

  override protected def encodes: DialectNode = AvroRootNode

  override val declares: Seq[DialectNode] = Seq(
    AvroRootNode
  )
  override protected def declaredNodes: Map[String, DialectNode] = Map.empty
}

object AvroRootNode extends DialectNode {
  override def nodeTypeMapping: String = AvroSchemaDocumentModel.`type`.head.iri()
  override def name                    = "AnyShape"
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(AvroDialect.DialectLocation + "#/declarations/ShapeNode/inherits")
      .withNodePropertyMapping(ShapeModel.Inherits.value.iri())
      .withName("type")
      .withMinCount(1)
      .withEnum(
        Seq(
          "null",
          "boolean",
          "int",
          "long",
          "float",
          "double",
          "bytes",
          "string",
          "record",
          "enum",
          "array",
          "map",
          "fixed"
        )
      )
      .withLiteralRange(xsdString.iri())
  )
}
