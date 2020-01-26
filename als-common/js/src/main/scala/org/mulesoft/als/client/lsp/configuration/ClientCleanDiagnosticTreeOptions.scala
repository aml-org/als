package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.lsp.feature.diagnostic.CleanDiagnosticTreeOptions

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientCleanDiagnosticTreeOptions extends js.Object {

  def supported: Boolean = js.native
}

object ClientCleanDiagnosticTreeOptions {
  def apply(internal: CleanDiagnosticTreeOptions): ClientCleanDiagnosticTreeOptions = {
    js.Dynamic
      .literal(
        supported = internal.supported
      )
      .asInstanceOf[ClientCleanDiagnosticTreeOptions]
  }
}
// $COVERAGE-ON$