package org.mulesoft.als.client.lsp.feature.completion

import org.mulesoft.als.client.lsp.common.{ClientPosition, ClientTextDocumentIdentifier}
import org.mulesoft.lsp.feature.completion.CompletionParams

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._

@js.native
trait ClientCompletionParams extends js.Object {
  def textDocument: ClientTextDocumentIdentifier   = js.native
  def position: ClientPosition                     = js.native
  def context: js.UndefOr[ClientCompletionContext] = js.native
}

object ClientCompletionParams {
  def apply(internal: CompletionParams): ClientCompletionParams =
    js.Dynamic
      .literal(
        textDocument = internal.textDocument.toClient,
        position = internal.position.toClient,
        context = internal.context.map(_.toClient).orUndefined
      )
      .asInstanceOf[ClientCompletionParams]
}
