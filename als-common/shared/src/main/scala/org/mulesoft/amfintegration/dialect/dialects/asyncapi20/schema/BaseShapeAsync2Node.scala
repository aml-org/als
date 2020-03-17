package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema

import amf.core.metamodel.domain.ShapeModel
import amf.core.vocabulary.Namespace.XsdTypes.xsdBoolean
import amf.dialects.oas.nodes.AMLExternalDocumentationObject
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.shapes.metamodel.{NodeShapeModel, ScalarShapeModel}
import org.mulesoft.als.suggestions.aml.dialects.asyncapi20.dialectLocation
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.base.{
  BaseAnyShapeNode,
  BaseArrayShapeNode,
  BaseNodeShapeNode,
  BaseNumberShapeNode,
  BaseShapeNode,
  BaseStringShapeNode
}
import amf.core.vocabulary.Namespace.XsdTypes._

trait BaseShapeAsync2Node extends BaseShapeNode {

  override def location: String = dialectLocation

  override def properties: Seq[PropertyMapping] =
    super.properties ++ Seq(
      PropertyMapping()
        .withId(location + "#/declarations/AnyShapeNode/deprecated")
        .withNodePropertyMapping(ShapeModel.Deprecated.value.iri())
        .withName("deprecated")
        .withLiteralRange(xsdBoolean.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/AnyShapeNode/externalDocs")
        .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
        .withName("externalDocs")
        .withObjectRange(Seq(AMLExternalDocumentationObject.id))
    )
}

object BaseShapeAsync2Node extends BaseShapeAsync2Node

object AnyShapeAsync2Node extends BaseAnyShapeNode with BaseShapeAsync2Node

object ArrayShapeAsync2Node extends BaseArrayShapeNode with BaseShapeAsync2Node

object NodeShapeAsync2Node extends BaseNodeShapeNode with BaseShapeAsync2Node {
  override def properties: Seq[PropertyMapping] =
    super.properties ++ Seq(
      PropertyMapping()
        .withId(location + "#/declarations/AnyShapeNode/discriminator")
        .withNodePropertyMapping(NodeShapeModel.Discriminator.value.iri())
        .withName("discriminator")
        .withLiteralRange(xsdString.iri()))
}

object NumberShapeAsync2Node extends BaseNumberShapeNode with BaseShapeAsync2Node {
  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + "#/declarations/AnyShapeNode/format")
      .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
      .withName("format")
      .withEnum(
        Seq(
          "int32",
          "int64",
          "float",
          "double"
        ))
      .withLiteralRange(xsdString.iri())
  )
}

object StringShapeAsync2Node extends BaseStringShapeNode with BaseShapeAsync2Node {
  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + "#/declarations/AnyShapeNode/format")
      .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
      .withName("format")
      .withEnum(
        Seq(
          "byte",
          "binary",
          "date",
          "date-time",
          "password"
        ))
      .withLiteralRange(xsdString.iri())
  )
}
