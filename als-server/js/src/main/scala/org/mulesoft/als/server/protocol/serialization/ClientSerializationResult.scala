package org.mulesoft.als.server.protocol.serialization

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientSerializationResult extends js.Object {

  def model: js.Any = js.native
}

object ClientSerializationResult {
  def apply(internal: js.Any): ClientSerializationResult =
    js.Dynamic
      .literal(
        model = internal
      )
      .asInstanceOf[ClientSerializationResult]
}

// $COVERAGE-ON$