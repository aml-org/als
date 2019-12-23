package org.mulesoft.als.client.lsp.feature.completion

import scala.scalajs.js

@js.native
trait ClientCompletionList extends js.Object {
  def items: js.Array[ClientCompletionItem] = js.native
  def isIncomplete: Boolean                 = js.native
}
