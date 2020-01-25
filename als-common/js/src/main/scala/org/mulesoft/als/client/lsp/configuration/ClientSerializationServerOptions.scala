package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.lsp.feature.serialization.SerializationServerOptions

import scala.scalajs.js

// $COVERAGE-OFF$
@js.native
trait ClientSerializationServerOptions extends js.Object {

  def supportsSerialization: Boolean = js.native
}

object ClientSerializationServerOptions {
  def apply(internal: SerializationServerOptions): ClientSerializationServerOptions =
    js.Dynamic
      .literal(supportsSerialization = internal.supportsSerialization)
      .asInstanceOf[ClientSerializationServerOptions]
}
// $COVERAGE-ON$