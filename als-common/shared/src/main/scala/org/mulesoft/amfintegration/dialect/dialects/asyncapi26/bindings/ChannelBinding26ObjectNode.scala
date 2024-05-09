package org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings

import amf.apicontract.internal.metamodel.domain.bindings.ChannelBindingModel

object ChannelBinding26ObjectNode extends BindingObjectNode26 {
  override protected def keys: Seq[String] = super.keys ++ Seq(
    "ibmmq",
    "googlepubsub",
    "anypointmq",
    "pulsar"
  )
  override def name: String = "ChannelBindingObjectNode"

  override def nodeTypeMapping: String = ChannelBindingModel.`type`.head.iri()
}
