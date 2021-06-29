package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.{ParameterModel, ServerModel}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect.DialectLocation

object Oas30ServerObject extends DialectNode {

  override def location: String        = OAS30Dialect.DialectLocation
  override def name: String            = "ServerObject"
  override def nodeTypeMapping: String = ServerModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] =
    Seq(
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ServerObject/url")
        .withName("url")
        .withNodePropertyMapping(ServerModel.Url.value.iri())
        .withMinCount(1)
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ServerObject/description")
        .withName("description")
        .withNodePropertyMapping(ServerModel.Description.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/ServerObject/variables")
        .withName("variables")
        .withNodePropertyMapping(ServerModel.Variables.value.iri())
        .withObjectRange(Seq(Oas30VariableObject.id))
        .withMapTermKeyProperty(ParameterModel.Name.value.iri())
    )
}
