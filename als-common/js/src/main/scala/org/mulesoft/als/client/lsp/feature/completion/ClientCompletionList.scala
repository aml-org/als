package org.mulesoft.als.client.lsp.feature.completion

import org.mulesoft.als.client.convert.LspConverters._
import org.mulesoft.lsp.feature.completion.CompletionList
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel(name = "CompletionList")
class ClientCompletionList(private val internal: CompletionList) {
  def items: js.Array[ClientCompletionItem] = internal.items.map(toClientCompletionItem).toJSArray
  def isIncomplete: Boolean                 = internal.isIncomplete
}
