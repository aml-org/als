package org.mulesoft.lsp.feature.reference

import org.mulesoft.lsp.feature.common.{ClientPosition, ClientTextDocumentIdentifier}
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientReferenceParams extends js.Object {
  def textDocument: ClientTextDocumentIdentifier = js.native
  def position: ClientPosition                   = js.native
  def context: ClientReferenceContext            = js.native
}

object ClientReferenceParams {
  def apply(internal: ReferenceParams): ClientReferenceParams =
    js.Dynamic
      .literal(
        textDocument = internal.textDocument.toClient,
        position = internal.position.toClient,
        context = internal.context.toClient
      )
      .asInstanceOf[ClientReferenceParams]
}

// $COVERAGE-ON$
