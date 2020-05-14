package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.core.vocabulary.Namespace.XsdTypes.xsdString
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.shapes.metamodel.SchemaShapeModel
import amf.plugins.domain.webapi.metamodel.ParameterModel
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect.DialectLocation

object Oas30VariableObject extends DialectNode {

  override def location: String        = OAS30Dialect.DialectLocation
  override def name: String            = "VariableObject"
  override def nodeTypeMapping: String = ParameterModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/VariableObject/name")
      .withName("name")
      .withNodePropertyMapping(ParameterModel.Name.value.iri())
      .withMinCount(1)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/VariableObject/enum")
      .withName("enum")
      .withNodePropertyMapping(SchemaShapeModel.Values.value.iri())
      .withLiteralRange(xsdString.iri())
      .withAllowMultiple(true),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/VariableObject/default")
      .withName("default")
      .withNodePropertyMapping(DialectLocation + "#/declarations/VariableObject/default")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/VariableObject/description")
      .withName("description")
      .withNodePropertyMapping(DialectLocation + "#/declarations/VariableObject/description")
      .withLiteralRange(xsdString.iri())
  )
}
