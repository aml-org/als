package org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdString}
import amf.core.internal.metamodel.domain.ShapeModel
import amf.shapes.internal.domain.metamodel.{AnyShapeModel, NodeShapeModel, ScalarShapeModel}
import org.mulesoft.amfintegration.dialect.dialects.dialects.dialectLocation
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.base._
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.AMLExternalDocumentationObject

trait BaseShapeAsync2Node extends BaseShapeNode {

  override def location: String = dialectLocation

  val format: PropertyMapping =
    PropertyMapping()
      .withId(location + "#/declarations/AnyShapeNode/format")
      .withNodePropertyMapping(ScalarShapeModel.Format.value.iri())
      .withName("format")
      .withLiteralRange(xsdString.iri())

  override def properties: Seq[PropertyMapping] =
    super.properties ++ Seq(
      format,
      PropertyMapping()
        .withId(location + "#/declarations/AnyShapeNode/deprecated")
        .withNodePropertyMapping(ShapeModel.Deprecated.value.iri())
        .withName("deprecated")
        .withLiteralRange(xsdBoolean.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/AnyShapeNode/externalDocs")
        .withNodePropertyMapping(AnyShapeModel.Documentation.value.iri())
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
        .withLiteralRange(xsdString.iri())
    )
}

object NumberShapeAsync2Node extends BaseNumberShapeNode with BaseShapeAsync2Node {
  override val format: PropertyMapping =
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
        )
      )
      .withLiteralRange(xsdString.iri())
}

object StringShapeAsync2Node extends BaseStringShapeNode with BaseShapeAsync2Node {
  override val format: PropertyMapping =
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
        )
      )
      .withLiteralRange(xsdString.iri())
}
