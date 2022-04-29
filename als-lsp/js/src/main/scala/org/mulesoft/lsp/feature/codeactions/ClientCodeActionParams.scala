package org.mulesoft.lsp.feature.codeactions

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.common.{ClientRange, ClientTextDocumentIdentifier}

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCodeActionParams extends js.Object {
  def textDocument: ClientTextDocumentIdentifier = js.native

  def range: ClientRange = js.native

  def context: ClientCodeActionContext = js.native
}

object ClientCodeActionParams {
  def apply(internal: CodeActionParams): ClientCodeActionParams =
    js.Dynamic
      .literal(
        textDocument = internal.textDocument.toClient,
        range = internal.range.toClient,
        context = internal.context.toClient
      )
      .asInstanceOf[ClientCodeActionParams]
}
// $COVERAGE-ON$
