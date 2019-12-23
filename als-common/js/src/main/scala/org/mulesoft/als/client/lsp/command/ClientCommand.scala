package org.mulesoft.als.client.lsp.command

import scala.scalajs.js

@js.native
trait ClientCommand extends js.Object {
  def title: String = js.native

  def command: String = js.native

  def arguments: js.UndefOr[js.Array[Any]] = js.native
}
