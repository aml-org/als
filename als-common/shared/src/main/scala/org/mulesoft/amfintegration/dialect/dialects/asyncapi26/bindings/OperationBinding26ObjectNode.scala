package org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings

import amf.apicontract.internal.metamodel.domain.bindings.OperationBindingModel

object OperationBinding26ObjectNode extends BindingObjectNode26 {
  override protected def keys: Seq[String] = super.keys ++ Seq(
    "solace"
  )
  override def name: String = "OperationBindingObjectNode"

  override def nodeTypeMapping: String = OperationBindingModel.`type`.head.iri()
}
