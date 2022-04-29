package org.mulesoft.als.server.protocol.textsync

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDidFocusParams extends js.Object {
  def uri: String      = js.native
  def version: Integer = js.native
}

object ClientDidFocusParams {
  def apply(internal: DidFocusParams): ClientDidFocusParams =
    js.Dynamic
      .literal(
        uri = internal.uri,
        version = internal.version
      )
      .asInstanceOf[ClientDidFocusParams]
}

// $COVERAGE-ON$
