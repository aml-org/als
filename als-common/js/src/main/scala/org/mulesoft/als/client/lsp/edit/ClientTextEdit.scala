package org.mulesoft.als.client.lsp.edit

import org.mulesoft.lsp.common.Range

import scala.scalajs.js

@js.native
trait ClientTextEdit extends js.Object {
  def range: Range    = js.native
  def newText: String = js.native
}
