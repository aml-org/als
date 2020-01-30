package org.mulesoft.als.server.protocol.serialization

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientSerializationMessage extends js.Object {

  def model: js.Any = js.native
}

object ClientSerializationMessage {
  def apply(internal: js.Any): ClientSerializationMessage =
    js.Dynamic
      .literal(
        model = internal
      )
      .asInstanceOf[ClientSerializationMessage]
}

// $COVERAGE-ON$