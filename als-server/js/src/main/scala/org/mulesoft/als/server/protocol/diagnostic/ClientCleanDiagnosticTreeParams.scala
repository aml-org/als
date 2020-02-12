package org.mulesoft.als.server.protocol.diagnostic

import org.mulesoft.als.server.feature.diagnostic.CleanDiagnosticTreeParams
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.common.ClientTextDocumentIdentifier

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCleanDiagnosticTreeParams extends js.Object {
  def textDocument: ClientTextDocumentIdentifier = js.native
}

object ClientCleanDiagnosticTreeParams {
  def apply(internal: CleanDiagnosticTreeParams): ClientCleanDiagnosticTreeParams = {
    js.Dynamic
      .literal(
        textDocument = internal.textDocument.toClient
      )
      .asInstanceOf[ClientCleanDiagnosticTreeParams]
  }
}
// $COVERAGE-ON$
