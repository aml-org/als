package org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings

import amf.apicontract.internal.metamodel.domain.bindings.MessageBindingModel

object MessageBinding26ObjectNode extends BindingObjectNode26 {
  override protected def keys: Seq[String] = super.keys ++ Seq(
    "ibmmq",
    "googlepubsub",
    "anypointmq"
  )
  override def name: String = "MessageBindingObjectNode"

  override def nodeTypeMapping: String = MessageBindingModel.`type`.head.iri()

}
