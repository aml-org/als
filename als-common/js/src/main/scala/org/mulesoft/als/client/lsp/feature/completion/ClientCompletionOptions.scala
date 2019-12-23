package org.mulesoft.als.client.lsp.feature.completion

import scala.scalajs.js

@js.native
trait ClientCompletionOptions extends js.Object {
  def resolveProvider: js.UndefOr[Boolean]          = js.native
  def triggerCharacters: js.UndefOr[js.Array[Char]] = js.native
}
