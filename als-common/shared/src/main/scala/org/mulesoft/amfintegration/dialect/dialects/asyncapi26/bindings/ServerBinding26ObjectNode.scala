package org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings

import amf.apicontract.internal.metamodel.domain.bindings.ServerBindingModel

object ServerBinding26ObjectNode extends BindingObjectNode26 {
  override def name: String = "ServerBindingObjectNode"

  override protected def keys: Seq[String] = super.keys ++ Seq(
    "ibmmq",
    "ibmmq-secure",
    "solace",
    "pulsar"
  )

  override def nodeTypeMapping: String = ServerBindingModel.`type`.head.iri()
}
