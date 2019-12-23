package org.mulesoft.als.client.lsp.feature.completion

import org.mulesoft.als.client.convert.LspConverters._
import org.mulesoft.lsp.common.{Position, TextDocumentIdentifier}
import org.mulesoft.lsp.feature.completion.CompletionParams

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel(name = "CompletionParams")
class ClientCompletionParams(private val internal: CompletionParams) {
  def textDocument: TextDocumentIdentifier         = internal.textDocument
  def position: Position                           = internal.position
  def context: js.UndefOr[ClientCompletionContext] = internal.context.map(toClientCompletionContext).orUndefined
}
