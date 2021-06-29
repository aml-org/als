package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.{ParameterModel, ServerModel}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings.ServerBindingsObjectNode
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object AsyncApiServerObject extends DialectNode {

  override def name: String = "ServerObject"

  override def nodeTypeMapping: String = ServerModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] =
    Seq(
      PropertyMapping()
        .withId(location + "#/declarations/ServerObject/name")
        .withName("name")
        .withNodePropertyMapping(ServerModel.Name.value.iri())
        .withMinCount(1)
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/ServerObject/description")
        .withName("description")
        .withNodePropertyMapping(ServerModel.Description.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/ServerObject/url")
        .withName("url")
        .withNodePropertyMapping(ServerModel.Url.value.iri())
        .withMinCount(1)
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/ServerObject/protocol")
        .withName("protocol")
        .withNodePropertyMapping(ServerModel.Protocol.value.iri())
        .withMinCount(1)
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/ServerObject/protocolVersion")
        .withName("protocolVersion")
        .withNodePropertyMapping(ServerModel.ProtocolVersion.value.iri())
        .withMinCount(1)
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/ServerObject/variables")
        .withName("variables")
        .withNodePropertyMapping(ServerModel.Variables.value.iri())
        .withObjectRange(Seq(AsyncApiVariableObject.id))
        .withMapTermKeyProperty(ParameterModel.Name.value.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/ServerObject/security")
        .withName("security")
        .withNodePropertyMapping(ServerModel.Security.value.iri())
        .withLiteralRange(xsdString.iri())
        .withAllowMultiple(true),
      PropertyMapping()
        .withId(location + "#/declarations/ServerObject/bindings")
        .withName("bindings")
        .withNodePropertyMapping(ServerModel.Bindings.value.iri())
        .withObjectRange(Seq(ServerBindingsObjectNode.id))
    )
}
