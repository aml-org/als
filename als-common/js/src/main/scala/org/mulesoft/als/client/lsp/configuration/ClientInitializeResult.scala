package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.configuration.InitializeResult

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientInitializeResult extends js.Object {
  def capabilities: ClientServerCapabilities = js.native
}

object ClientInitializeResult {
  def apply(internal: InitializeResult): ClientInitializeResult =
    js.Dynamic
      .literal(
        capabilities = internal.capabilities.toClient
      )
      .asInstanceOf[ClientInitializeResult]
}

// $COVERAGE-ON$