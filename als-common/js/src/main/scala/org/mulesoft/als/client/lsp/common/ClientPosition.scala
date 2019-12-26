package org.mulesoft.als.client.lsp.common

import org.mulesoft.lsp.common.Position

import scala.scalajs.js

@js.native
trait ClientPosition extends js.Object {
  def line: Int      = js.native
  def character: Int = js.native
}

object ClientPosition {
  def apply(internal: Position): ClientPosition =
    js.Dynamic
      .literal(line = internal.line, character = internal.character)
      .asInstanceOf[ClientPosition]
}
