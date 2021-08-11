package org.mulesoft.lsp.feature.diagnostic

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDiagnosticCodeDescription extends js.Object {
  def href: String = js.native
}

object ClientDiagnosticCodeDescription {
  def apply(internal: String): Unit = {
    js.Dynamic
      .literal(
        href = internal
      )
      .asInstanceOf[ClientDiagnosticCodeDescription]
  }
}

// $COVERAGE-ON$
