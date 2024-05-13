package org.mulesoft.amfintegration.dialect.dialects.asyncapi26

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.EndPointModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.ChannelObject

object Channel26Object extends ChannelObject {
  override protected val operationId: String = Operation26Object.id

  override def properties: Seq[PropertyMapping] = super.properties :+ PropertyMapping()
    .withId(location + "#/declarations/Channel/servers")
    .withName("servers")
    .withNodePropertyMapping(EndPointModel.Servers.value.iri())
    .withAllowMultiple(true)
    .withLiteralRange(xsdString.iri())
}
