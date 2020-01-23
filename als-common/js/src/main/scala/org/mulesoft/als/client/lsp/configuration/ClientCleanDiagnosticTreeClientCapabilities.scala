package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.lsp.feature.diagnostic.CleanDiagnosticTreeClientCapabilities

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientCleanDiagnosticTreeClientCapabilities extends js.Object {
  def enableCleanDiagnostic: Boolean = js.native
}

object ClientCleanDiagnosticTreeClientCapabilities {
  def apply(internal: CleanDiagnosticTreeClientCapabilities): ClientCleanDiagnosticTreeClientCapabilities = {
    js.Dynamic
      .literal(
        enableCleanDiagnostic = internal.enableCleanDiagnostic
      )
      .asInstanceOf[ClientCleanDiagnosticTreeClientCapabilities]
  }
}
// $COVERAGE-ON$