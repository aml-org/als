package org.mulesoft.als.client.lsp.feature.completion

import org.mulesoft.lsp.feature.completion.CompletionOptions
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel(name = "CompletionOptions")
class ClientCompletionOptions(private val internal: CompletionOptions) {
  def resolveProvider: js.UndefOr[Boolean]          = internal.resolveProvider.orUndefined
  def triggerCharacters: js.UndefOr[js.Array[Char]] = internal.triggerCharacters.map(_.toJSArray).orUndefined
}
