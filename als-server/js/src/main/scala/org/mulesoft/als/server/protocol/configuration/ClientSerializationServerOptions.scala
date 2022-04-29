package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.serialization.SerializationServerOptions

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
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
