package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.core.vocabulary.Namespace.XsdTypes._
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.ParameterModel
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object ParameterObjectNode extends DialectNode {
  override def name: String = "ParameterObjectNode"

  override def nodeTypeMapping: String = ParameterModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + "#/declarations/Parameter/name")
      .withName("name")
      .withNodePropertyMapping(ParameterModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/Parameter/description")
      .withName("description")
      .withNodePropertyMapping(ParameterModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/Parameter/schema")
      .withName("schema")
      .withNodePropertyMapping(ParameterModel.Schema.value.iri())
      .withObjectRange(Seq("")),
    PropertyMapping()
      .withId(location + "#/declarations/Parameter/location")
      .withName("location")
      .withNodePropertyMapping(ParameterModel.Binding.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}
