package org.mulesoft.als.client.lsp.command

import org.mulesoft.lsp

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("Command")
class ClientCommand(private val internal: lsp.command.Command) {
  def title: String = internal.title

  def command: String = internal.command

  def arguments: js.UndefOr[js.Array[Any]] = internal.arguments.map(_.toJSArray).orUndefined
}
