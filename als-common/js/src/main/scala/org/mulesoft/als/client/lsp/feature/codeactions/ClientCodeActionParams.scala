package org.mulesoft.als.client.lsp.feature.codeactions

import org.mulesoft.lsp.common.{Range, TextDocumentIdentifier}
import org.mulesoft.lsp.feature.codeactions.CodeActionParams

import scala.scalajs.js
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.common.{ClientRange, ClientTextDocumentIdentifier}

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
        context = internal.context.toClient,
      )
      .asInstanceOf[ClientCodeActionParams]
}