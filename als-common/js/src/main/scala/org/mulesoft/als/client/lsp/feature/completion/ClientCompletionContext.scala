package org.mulesoft.als.client.lsp.feature.completion

import org.mulesoft.lsp.feature.completion.CompletionContext

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("CompletionContext")
class ClientCompletionContext(private val internal: CompletionContext) {
  def triggerKind: Int                   = internal.triggerKind.id
  def triggerCharacter: js.UndefOr[Char] = internal.triggerCharacter.orUndefined
}
