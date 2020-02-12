package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.serialization.SerializationClientCapabilities

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientSerializationClientCapabilities extends js.Object {
  def acceptsNotification: Boolean = js.native
}

object ClientSerializationClientCapabilities {
  def apply(internal: SerializationClientCapabilities): ClientSerializationClientCapabilities = {
    js.Dynamic
      .literal(
        acceptsNotification = internal.acceptsNotification
      )
      .asInstanceOf[ClientSerializationClientCapabilities]
  }
}
// $COVERAGE-ON$
