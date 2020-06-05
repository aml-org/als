package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.core.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdString}
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.{ParameterModel, PayloadModel}
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.DialectLocation

object Oas20BodyParameterObject extends DialectNode {

  override def name: String            = "BodyParameterObject"
  override def nodeTypeMapping: String = PayloadModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/BodyParameterObject/description")
      .withName("description")
      .withNodePropertyMapping(ParameterModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/BodyParameterObject/required")
      .withName("required")
      .withMinCount(1)
      .withNodePropertyMapping(ParameterModel.Required.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/BodyParameterObject/parameterName")
      .withName("name")
      .withMinCount(1)
      .withNodePropertyMapping(ParameterModel.ParameterName.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/BodyParameterObject/binding")
      .withName("in")
      .withMinCount(1)
      .withEnum(Seq(
        "body"
      ))
      .withNodePropertyMapping(ParameterModel.Binding.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/BodyParameterObject/schema")
      .withName("schema")
      .withMinCount(1)
      .withNodePropertyMapping(ParameterModel.Schema.value.iri())
      .withObjectRange(Seq(
        Oas20SchemaObject.id
      ))
  )
}
