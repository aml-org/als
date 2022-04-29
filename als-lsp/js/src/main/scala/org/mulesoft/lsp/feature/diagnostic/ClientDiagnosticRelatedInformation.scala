package org.mulesoft.lsp.feature.diagnostic

import org.mulesoft.lsp.feature.common.ClientLocation
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDiagnosticRelatedInformation extends js.Object {
  def location: ClientLocation = js.native
  def message: String          = js.native
}

object ClientDiagnosticRelatedInformation {
  def apply(internal: DiagnosticRelatedInformation): ClientDiagnosticRelatedInformation =
    js.Dynamic
      .literal(
        location = internal.location.toClient,
        message = internal.message
      )
      .asInstanceOf[ClientDiagnosticRelatedInformation]
}

// $COVERAGE-ON$
