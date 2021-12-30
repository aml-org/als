package org.mulesoft.als.server.protocol.serialization

import org.mulesoft.als.server.feature.serialization.SerializedDocument

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientSerializedDocument extends js.Object {
  def uri: String   = js.native
  def model: String = js.native

}

object ClientSerializedDocument {
  def apply(internal: SerializedDocument): ClientSerializedDocument = {
    js.Dynamic
      .literal(
        uri = internal.uri,
        model = internal.model
      )
      .asInstanceOf[ClientSerializedDocument]
  }
}
// $COVERAGE-ON$
