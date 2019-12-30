package org.mulesoft.als.client.lsp.feature.diagnostic

import org.mulesoft.als.client.lsp.common.ClientLocation
import org.mulesoft.lsp.feature.diagnostic.DiagnosticRelatedInformation
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._

import scala.scalajs.js

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
