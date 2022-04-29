package org.mulesoft.lsp.workspace

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientExecuteCommandParams extends js.Object {
  def command: String = js.native

  def arguments: js.Array[js.Any] = js.native
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

// $COVERAGE-ON$
