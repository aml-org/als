package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.{EndPointModel, ParameterModel}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings.ChannelBindingsObjectNode
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

trait ChannelObject extends DialectNode {
  override def name: String = "ChannelObject"

  override def nodeTypeMapping: String = EndPointModel.`type`.head.iri()
  protected val operationId: String    = Operation20Object.id
  override def properties: Seq[PropertyMapping] = {
    Seq(
      PropertyMapping()
        .withId(location + "#/declarations/Channel/channelPath")
        .withName("channelPath")
        .withNodePropertyMapping(EndPointModel.Path.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/Channel/description")
        .withName("description")
        .withNodePropertyMapping(EndPointModel.Description.value.iri())
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/Channel/subscribe")
        .withName("subscribe")
        .withNodePropertyMapping(EndPointModel.Operations.value.iri())
        .withObjectRange(Seq(operationId)),
      PropertyMapping()
        .withId(location + "#/declarations/Channel/publish")
        .withName("publish")
        .withNodePropertyMapping(EndPointModel.Operations.value.iri())
        .withObjectRange(Seq(operationId)),
      PropertyMapping()
        .withId(location + "#/declarations/Channel/parameters")
        .withName("parameters")
        .withNodePropertyMapping(EndPointModel.Parameters.value.iri())
        .withObjectRange(Seq(ParameterObjectNode.id))
        .withMapTermKeyProperty(ParameterModel.Name.value.iri()),
      PropertyMapping()
        .withId(location + "#/declarations/Channel/bindings")
        .withName("bindings")
        .withNodePropertyMapping(EndPointModel.Bindings.value.iri())
        .withObjectRange(Seq(ChannelBindingsObjectNode.id))
    )
  }
}

object Channel20Object extends ChannelObject
