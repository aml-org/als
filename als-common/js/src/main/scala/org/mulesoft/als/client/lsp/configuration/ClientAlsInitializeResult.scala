package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.configuration.AlsInitializeResult

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
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