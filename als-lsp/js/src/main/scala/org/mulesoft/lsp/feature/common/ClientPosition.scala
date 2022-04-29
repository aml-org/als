package org.mulesoft.lsp.feature.common

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

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

// $COVERAGE-ON$
