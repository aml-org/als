package org.mulesoft.als.client.lsp.feature.diagnostic

import org.mulesoft.lsp.feature.diagnostic.DiagnosticClientCapabilities

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDiagnosticClientCapabilities extends js.Object {
  def relatedInformation: js.UndefOr[Boolean] = js.native
}

object ClientDiagnosticClientCapabilities {
  def apply(internal: DiagnosticClientCapabilities): ClientDiagnosticClientCapabilities =
    js.Dynamic
      .literal(
        relatedInformation = internal.relatedInformation.orUndefined
      )
      .asInstanceOf[ClientDiagnosticClientCapabilities]
}
// $COVERAGE-ON$