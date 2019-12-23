package org.mulesoft.als.client.lsp.feature.completion

import org.mulesoft.lsp.common.{Position, TextDocumentIdentifier}

import scala.scalajs.js

@js.native
trait ClientCompletionParams extends js.Object {
  def textDocument: TextDocumentIdentifier         = js.native
  def position: Position                           = js.native
  def context: js.UndefOr[ClientCompletionContext] = js.native
}
