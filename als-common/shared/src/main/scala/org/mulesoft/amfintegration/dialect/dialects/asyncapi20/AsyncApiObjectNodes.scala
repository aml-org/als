package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.core.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdString}
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import amf.plugins.domain.shapes.metamodel.{AnyShapeModel, ScalarShapeModel}
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.JsonSchemaDraft7DialectNodes

object AsyncApiObjectNodes extends JsonSchemaDraft7DialectNodes {
  override val DialectLocation: String = AsyncApi20Dialect.DialectLocation

  override val AnyShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/AnyShapeNode")
    .withName("AnyShapeNode")
    .withNodeTypeMapping(AnyShapeModel.`type`.head.iri())
    .withPropertiesMapping(anyShapeProperties ++ Seq(
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
        .withObjectRange(Seq(ShapeNode.id))
    ))
}
