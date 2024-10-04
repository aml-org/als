package org.mulesoft.amfintegration.dialect.dialects.avro

import amf.aml.client.scala.model.domain.{DocumentsModel, PropertyMapping}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdInteger, xsdString}
import amf.core.internal.metamodel.domain.ShapeModel
import amf.core.internal.metamodel.domain.extensions.PropertyShapeModel
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import amf.shapes.internal.document.metamodel.AvroSchemaDocumentModel
import amf.shapes.internal.domain.metamodel.{AnyShapeModel, ArrayShapeModel, NodeShapeModel, ScalarShapeModel}
import org.mulesoft.amfintegration.dialect.BaseDialect
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroFieldNode.PropertyShapeAvroNode
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object AvroDialect extends BaseDialect {
  val avroTypes = Seq(
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

  val avroLogicalTypes = Seq(
    "decimal",
    "uuid",
    "date",
    "time-millis",
    "time-micros",
    "timestamp-millis",
    "timestamp-micros",
    "local-timestamp-millis",
    "local-timestamp-micros",
    "duration"
  )
  override def DialectLocation: String = "file://vocabularies/dialects/avro.yaml"

  final val inheritsId: String       = AvroDialect.DialectLocation + "#/declarations/ShapeNode/inherits"
  final val avro190MediaType: String = "application/vnd.apache.avro;version=1.9.0"

  override protected val name: String    = "avro"
  override protected val version: String = ""

  override protected def emptyDocument: DocumentsModel =
    DocumentsModel()
      .withId(DialectLocation + "#/documents")
      .withReferenceStyle(ReferenceStyles.JSONSCHEMA)

  override protected def encodes: DialectNode = AvroRootNode

  override val declares: Seq[DialectNode] = Seq(
    AvroRootNode,
    AvroRecordNode,
    AvroArrayNode,
    AvroAnyNode,
    AvroPrimitiveNode,
    PropertyShapeAvroNode
  )
  override protected def declaredNodes: Map[String, DialectNode] = Map.empty
}

trait AvroTypedNode extends DialectNode {
  override def name = "AnyShape"
  protected def defaultMapping: PropertyMapping = PropertyMapping()
    .withId(AvroDialect.DialectLocation + "#/declarations/ShapeNode/defaultValue")
    .withNodePropertyMapping(ShapeModel.Default.value.iri())
    .withName("default")
    .withObjectRange(Seq())

  protected val docMapping: PropertyMapping = PropertyMapping()
    .withId(AvroDialect.DialectLocation + "#/declarations/doc")
    .withName("doc")
    .withLiteralRange(xsdString.iri())

  val nameMapping = PropertyMapping()
    .withId(AvroDialect.DialectLocation + "#/declarations/name")
    .withName("name")
    .withLiteralRange(xsdString.iri())
    .withMinCount(1)

  protected val namespaceMapping: PropertyMapping = PropertyMapping()
    .withId(AvroDialect.DialectLocation + "#/declarations/namespace")
    .withName("namespace")
    .withLiteralRange(xsdString.iri())

  protected val aliasesMapping: PropertyMapping = PropertyMapping()
    .withId(AvroDialect.DialectLocation + "#/declarations/aliases")
    .withName("aliases")
    .withAllowMultiple(true)
    .withLiteralRange(xsdString.iri())
  override def properties: Seq[PropertyMapping] = {
    Seq(
      PropertyMapping()
        .withId(AvroDialect.inheritsId)
        .withNodePropertyMapping(ShapeModel.Inherits.value.iri())
        .withName("type")
        .withMinCount(1)
        .withEnum(
          AvroDialect.avroTypes
        )
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(AvroDialect.DialectLocation + "#/declarations/logicalType")
        .withNodePropertyMapping(ShapeModel.Inherits.value.iri())
        .withName("logicalType")
        .withEnum(
          AvroDialect.avroLogicalTypes
        )
        .withLiteralRange(xsdString.iri()),
      defaultMapping,
      nameMapping
    )
  }
}

object AvroRootNode extends AvroTypedNode {
  override def nodeTypeMapping: String = AvroSchemaDocumentModel.`type`.head.iri()
}

object AvroRecordNode extends AvroTypedNode {
  override def nodeTypeMapping: String = NodeShapeModel.`type`.head.iri()
  override def name                    = "NodeShape"

  override def properties: Seq[PropertyMapping] = {
    super.properties :+
      PropertyMapping()
        .withId(AvroDialect.inheritsId)
        .withNodePropertyMapping(ShapeModel.Inherits.value.iri())
        .withName("fields")
        .withAllowMultiple(true)
        .withMinCount(1)
        .withObjectRange(Seq(AvroRootNode.id)) :+
      aliasesMapping :+
      docMapping :+
      namespaceMapping
  }
}

object AvroEnumNode extends AvroTypedNode {
  override def nodeTypeMapping: String = ScalarShapeModel.`type`.head.iri()
  override def name                    = "ScalarShape"

  override def properties: Seq[PropertyMapping] = {
    super.properties :+
      PropertyMapping()
        .withId(AvroDialect.DialectLocation + "#/declarations/ShapeNode/symbols")
        .withNodePropertyMapping(ShapeModel.Values.value.iri())
        .withName("symbols")
        .withAllowMultiple(true)
        .withLiteralRange(xsdString.iri()) :+
      namespaceMapping :+
      docMapping
  }
}
object AvroPrimitiveNode extends AvroTypedNode {
  override def nodeTypeMapping: String = ScalarShapeModel.`type`.head.iri()
  override def name                    = "ScalarShape"
  override def properties: Seq[PropertyMapping] = {
    super.properties :+
      namespaceMapping :+
      docMapping
  }
}

object AvroFixedNode extends AvroTypedNode {
  override def nodeTypeMapping: String = ScalarShapeModel.`type`.head.iri()
  override def name                    = "ScalarShape"

  override def properties: Seq[PropertyMapping] = {
    super.properties :+
      PropertyMapping()
        .withId(AvroDialect.DialectLocation + "#/declarations/ShapeNode/size")
        .withNodePropertyMapping(ShapeModel.Values.value.iri())
        .withName("size")
        .withLiteralRange(xsdInteger.iri()) :+
      namespaceMapping :+
      aliasesMapping
  }
}

object AvroMapNode extends AvroTypedNode {
  override def nodeTypeMapping: String = NodeShapeModel.`type`.head.iri()
  override def name                    = "NodeShape"
  override def properties: Seq[PropertyMapping] = super.properties :+
    PropertyMapping()
      .withId(AvroDialect.inheritsId)
      .withNodePropertyMapping(ShapeModel.Inherits.value.iri())
      .withName("values")
      .withEnum(
        AvroDialect.avroTypes
      )
      .withLiteralRange(xsdString.iri())
}

object AvroArrayNode extends AvroTypedNode {
  override def nodeTypeMapping: String = ArrayShapeModel.`type`.head.iri()
  override def name                    = "ArrayShape"
  override def properties: Seq[PropertyMapping] = super.properties :+
    PropertyMapping()
      .withId(AvroDialect.inheritsId)
      .withNodePropertyMapping(ShapeModel.Inherits.value.iri())
      .withName("items")
      .withEnum(
        AvroDialect.avroTypes
      )
      .withLiteralRange(xsdString.iri())

  override protected def defaultMapping: PropertyMapping = super.defaultMapping.withAllowMultiple(true)
}
object AvroAnyNode extends AvroTypedNode {
  override def nodeTypeMapping: String = AnyShapeModel.`type`.head.iri()
  override def name                    = "AnyShape"
}

object AvroFieldNode extends AvroTypedNode {
  override def nodeTypeMapping: String = AvroDialect.DialectLocation + "#/declarations/field"
  override def name                    = "AvroField"
  override def properties: Seq[PropertyMapping] =
    super.properties :+
      docMapping

  object PropertyShapeAvroNode extends AvroTypedNode {
    override def nodeTypeMapping: String          = PropertyShapeModel.`type`.head.iri()
    override def name                             = "PropertyShape"
    override def properties: Seq[PropertyMapping] = Seq.empty
  }
}
