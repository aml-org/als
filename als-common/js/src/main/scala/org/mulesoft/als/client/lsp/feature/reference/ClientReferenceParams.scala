package org.mulesoft.als.client.lsp.feature.reference

import org.mulesoft.lsp.common.{Position, TextDocumentIdentifier, TextDocumentPositionParams}

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.common.{ClientPosition, ClientTextDocumentIdentifier}
import org.mulesoft.lsp.feature.reference.ReferenceParams

import js.JSConverters._

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
