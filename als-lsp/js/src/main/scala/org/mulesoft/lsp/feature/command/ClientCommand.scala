package org.mulesoft.lsp.feature.command

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCommand extends js.Object {
  def title: String = js.native

  def command: String = js.native

  def arguments: js.UndefOr[js.Array[js.Object]] = js.native
}

object ClientCommand {
  def apply(internal: Command): ClientCommand =
    js.Dynamic
      .literal(
        title = internal.title,
        command = internal.command,
        arguments = internal.arguments.map(_.toJSArray).orUndefined
      )
      .asInstanceOf[ClientCommand]
}

// $COVERAGE-ON$
