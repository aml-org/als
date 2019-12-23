package org.mulesoft.als.client.lsp.command

import org.mulesoft.lsp.command.Command

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

@js.native
class ClientCommand() extends js.Object {
  def title: String = js.native

  def command: String = js.native

  def arguments: js.UndefOr[js.Array[Any]] = js.native
}
