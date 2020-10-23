package org.mulesoft.als.server.protocol.serialization

import org.mulesoft.als.server.feature.serialization.SerializationResult

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientSerializationResult extends js.Object {
  def uri: String   = js.native
  def model: js.Any = js.native
}

object ClientSerializationResult {
  def apply[S](internal: SerializationResult[S]): ClientSerializationResult =
    js.Dynamic
      .literal(
        uri = internal.uri,
        model = internal.model.asInstanceOf[js.Any]
      )
      .asInstanceOf[ClientSerializationResult]
}

// $COVERAGE-ON$
