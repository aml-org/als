package org.mulesoft.lsp.feature.completion

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.lsp.feature.common.{ClientPosition, ClientTextDocumentIdentifier}
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

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

// $COVERAGE-ON$
