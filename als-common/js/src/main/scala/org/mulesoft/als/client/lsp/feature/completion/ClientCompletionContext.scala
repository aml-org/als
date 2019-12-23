package org.mulesoft.als.client.lsp.feature.completion

import scala.scalajs.js

@js.native
trait ClientCompletionContext extends js.Object {
  def triggerKind: Int                   = js.native
  def triggerCharacter: js.UndefOr[Char] = js.native
}
