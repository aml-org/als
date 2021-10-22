package org.mulesoft.amfintegration.dialect.dialects.raml.raml08

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping, PublicNodeMapping}
import amf.core.client.scala.vocabulary.Namespace
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdFloat, xsdInteger, xsdString}
import amf.core.internal.metamodel.domain.ShapeModel
import amf.core.internal.metamodel.domain.extensions.PropertyShapeModel
import amf.shapes.internal.domain.metamodel.{
  AnyShapeModel,
  FileShapeModel,
  NilShapeModel,
  NodeShapeModel,
  ScalarShapeModel,
  UnionShapeModel
}
import org.mulesoft.amfintegration.dialect.dialects.raml.RamlDialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08DialectNodes.ExampleNode

object Raml08TypesDialect {

  // hack to force object initialization in amf and avoid exception
  private val orignalId           = Raml08Dialect().id
  final val ImplicitField: String = (Namespace.Meta + "implicit").iri()

  private object Raml08Dialect extends RamlDialect {

    override val dialectLocation = "file://vocabularies/dialects/raml08.yaml"

    // Marking syntactic fields in the AST that are not directly mapped to properties in the model

    override protected lazy val rootId: String = Raml08DialectNodes.RootNode.id
    override protected lazy val dialectDeclares: Seq[NodeMapping] = Seq(
      Raml08DialectNodes.ExampleNode,
      Raml08DialectNodes.DataTypeNode,
      Raml08DialectNodes.DocumentationNode,
      Raml08DialectNodes.PayloadNode,
      Raml08DialectNodes.ResourceTypeNode,
      Raml08DialectNodes.TraitNode,
      Raml08DialectNodes.ResponseNode,
      Raml08DialectNodes.MethodNode,
      Raml08DialectNodes.ResourceNode,
      Raml08DialectNodes.RootNode
    )

    override protected val version: String = "0.8"
  }
  val DialectLocation = "file://parallel-als/vocabularies/dialects/raml08.yaml"

  val ShapeNodeId: String   = DialectLocation + "#/declarations/ShapeNode"
  val SchemasNodeId: String = DialectLocation + "#/declarations/SchemasNode"

  val shapeTypesProperty: PropertyMapping = PropertyMapping()
    .withId(DialectLocation + "#/declarations/ShapeNode/inherits")
    .withNodePropertyMapping(ShapeModel.Inherits.value.iri())
    .withName("type")
    .withMinCount(1)
    .withEnum(
      Seq(
        "string",
        "number",
        "integer",
        "boolean",
        "file",
        "date"
      ))
    .withLiteralRange(xsdString.iri())

  val schemasProperties: Seq[PropertyMapping] = Seq(shapeTypesProperty)

  val shapeProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ShapeNode/in")
      .withNodePropertyMapping(ShapeModel.Values.value.iri())
      .withName("enum")
      .withObjectRange(Seq(ShapeNodeId))
      .withAllowMultiple(true),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ShapeNode/default")
      .withNodePropertyMapping(ShapeModel.Default.value.iri())
      .withName("default")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ShapeNode/displayName")
      .withNodePropertyMapping(ShapeModel.DisplayName.value.iri())
      .withName("displayName")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/ShapeNode/description")
      .withNodePropertyMapping(ShapeModel.Description.value.iri())
      .withName("description")
      .withLiteralRange(xsdString.iri()),
    shapeTypesProperty
  )

  val ShapeNode: NodeMapping = NodeMapping()
    .withId(ShapeNodeId)
    .withName("ShapeNode")
    .withNodeTypeMapping(ShapeModel.`type`.head.iri())
    .withPropertiesMapping(shapeProperties)

  val SchemasNode: NodeMapping = NodeMapping()
    .withId(SchemasNodeId)
    .withName("SchemasNode")
    .withNodeTypeMapping(ShapeModel.`type`.head.iri())
    .withPropertiesMapping(schemasProperties)

  val anyShapeProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/AnyShapeNode/example")
      .withNodePropertyMapping(AnyShapeModel.Examples.value.iri())
      .withName("example")
      .withObjectRange(Seq(ExampleNode.id))
  ) ++ shapeProperties

  val AnyShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/AnyShapeNode")
    .withName("AnyShapeNode")
    .withNodeTypeMapping(AnyShapeModel.`type`.head.iri())
    .withPropertiesMapping(anyShapeProperties)

  val PropertyShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/PropertyShapeNode")
    .withName("PropertyShapeNode")
    .withNodeTypeMapping(PropertyShapeModel.`type`.head.iri())
    .withPropertiesMapping(
      anyShapeProperties ++ Seq(
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/PropertyShapeNode/required")
          .withNodePropertyMapping(PropertyShapeModel.MinCount.value.iri())
          .withName("required")
          .withLiteralRange(xsdBoolean.iri())
      ))

  val NodeShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/NodeShapeNode")
    .withName("NodeShapeNode")
    .withNodeTypeMapping(NodeShapeModel.`type`.head.iri())
    .withPropertiesMapping(
      anyShapeProperties ++ Seq(
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/NodeShapeNode/properties")
          .withNodePropertyMapping(NodeShapeModel.Properties.value.iri())
          .withName("properties")
          .withObjectRange(Seq(PropertyShapeNode.id))
          .withMapTermKeyProperty(PropertyShapeModel.Name.value.iri())
      ))

  val UnionShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/UnionShapeNode")
    .withName("UnionShapeNode")
    .withNodeTypeMapping(UnionShapeModel.`type`.head.iri())
    .withPropertiesMapping(anyShapeProperties)

  val ScalarShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/ScalarShapeNode")
    .withName("ScalarShapeNode")
    .withNodeTypeMapping(ScalarShapeModel.`type`.head.iri())
    .withPropertiesMapping(anyShapeProperties)

  val StringShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/StringShapeNode")
    .withName("StringShapeNode")
    .withNodeTypeMapping((Namespace.Shapes + "StringShape").iri())
    .withPropertiesMapping(anyShapeProperties ++ Seq(
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/pattern")
        .withNodePropertyMapping(ScalarShapeModel.Pattern.value.iri())
        .withName("pattern")
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/minLength")
        .withNodePropertyMapping(ScalarShapeModel.MinLength.value.iri())
        .withName("minLength")
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/maxLength")
        .withNodePropertyMapping(ScalarShapeModel.MaxLength.value.iri())
        .withName("maxLength")
        .withLiteralRange(xsdInteger.iri())
    ))

  val NumberShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/NumberShapeNode")
    .withName("NumberShapeNode")
    .withNodeTypeMapping((Namespace.Shapes + "NumberShape").iri())
    .withPropertiesMapping(anyShapeProperties ++ Seq(
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/minimum")
        .withNodePropertyMapping(ScalarShapeModel.Minimum.value.iri())
        .withName("minimum")
        .withLiteralRange(xsdFloat.iri()),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/maximun")
        .withNodePropertyMapping(ScalarShapeModel.Maximum.value.iri())
        .withName("maximum")
        .withLiteralRange(xsdFloat.iri()),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/format")
        .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
        .withName("format")
        .withEnum(
          Seq(
            "int8",
            "int16",
            "int32",
            "int64",
            "int",
            "long",
            "float",
            "double"
          ))
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/maximun")
        .withNodePropertyMapping(ScalarShapeModel.MultipleOf.value.iri())
        .withName("multipleOf")
        .withLiteralRange(xsdFloat.iri())
    ))

  val FileShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/FileShapeNode")
    .withName("FileShapeNode")
    .withNodeTypeMapping(FileShapeModel.`type`.head.iri())
    .withPropertiesMapping(anyShapeProperties ++ Seq(
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/fileTypes")
        .withNodePropertyMapping(FileShapeModel.FileTypes.value.iri())
        .withName("fileTypes")
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/minLength")
        .withNodePropertyMapping(FileShapeModel.MinLength.value.iri())
        .withName("minLength")
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ScalarShapeNode/maxLength")
        .withNodePropertyMapping(FileShapeModel.MaxLength.value.iri())
        .withName("maxLength")
        .withLiteralRange(xsdInteger.iri())
    ))

  val NilShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/NilShapeNode")
    .withName("NilShapeNode")
    .withNodeTypeMapping(NilShapeModel.`type`.head.iri())

  val dialect: Dialect = {
    val dialect = Raml08Dialect()
    dialect.withDeclares(
      dialect.declares ++
        Seq(
          ShapeNode,
          AnyShapeNode,
          PropertyShapeNode,
          NodeShapeNode,
          UnionShapeNode,
          StringShapeNode,
          NumberShapeNode,
          FileShapeNode,
          NilShapeNode,
          ScalarShapeNode,
          Raml08SecuritySchemesDialect.SecurityScheme,
          Raml08SecuritySchemesDialect.OAuth1Settings,
          Raml08SecuritySchemesDialect.OAuth2Settings,
          Raml08SecuritySchemesDialect.OAuth2Flows
        ))

    val declaredNodes = Seq(
      PublicNodeMapping()
        .withId(DialectLocation + "#/documents/resourceTypes")
        .withName("resourceTypes")
        .withMappedNode(Raml08DialectNodes.ResourceTypeNode.id),
      PublicNodeMapping()
        .withId(DialectLocation + "#/documents/schemas")
        .withName("schemas")
        .withMappedNode(ShapeNode.id),
      PublicNodeMapping()
        .withId(DialectLocation + "#/documents/traits")
        .withName("traits")
        .withMappedNode(Raml08DialectNodes.TraitNode.id),
      PublicNodeMapping()
        .withId(DialectLocation + "#/documents/securitySchemes")
        .withName("securitySchemes")
        .withMappedNode(Raml08SecuritySchemesDialect.SecurityScheme.id) // todo
    )

    dialect.documents().root().withDeclaredNodes(declaredNodes)

    dialect
      .documents()
    dialect
  }

  def apply(): Dialect = dialect
}
