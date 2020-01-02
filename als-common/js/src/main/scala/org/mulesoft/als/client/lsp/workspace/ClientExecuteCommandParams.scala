package org.mulesoft.als.client.lsp.workspace

import org.mulesoft.lsp.workspace.ExecuteCommandParams

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

@js.native
trait ClientExecuteCommandParams extends js.Object {
  def command: String = js.native

  def arguments: js.Array[String] = js.native
}

object ClientExecuteCommandParams {
  def apply(internal: ExecuteCommandParams): ClientExecuteCommandParams =
    js.Dynamic
      .literal(
        command = internal.command,
        arguments = internal.arguments.toJSArray
      )
      .asInstanceOf[ClientExecuteCommandParams]
}
