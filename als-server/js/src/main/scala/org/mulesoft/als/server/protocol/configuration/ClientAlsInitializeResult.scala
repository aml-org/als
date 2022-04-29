package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.protocol.convert.LspConvertersSharedToClient._

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientAlsInitializeResult extends js.Object {
  def capabilities: ClientAlsServerCapabilities = js.native
}

object ClientAlsInitializeResult {
  def apply(internal: AlsInitializeResult): ClientAlsInitializeResult =
    js.Dynamic
      .literal(
        capabilities = internal.capabilities.toClient
      )
      .asInstanceOf[ClientAlsInitializeResult]
}

// $COVERAGE-ON$
