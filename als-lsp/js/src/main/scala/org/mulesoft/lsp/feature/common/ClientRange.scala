package org.mulesoft.lsp.feature.common

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientRange extends js.Object {
  def start: ClientPosition = js.native

  def end: ClientPosition = js.native
}

object ClientRange {
  def apply(internal: Range): ClientRange =
    js.Dynamic
      .literal(start = internal.start.toClient, end = internal.end.toClient)
      .asInstanceOf[ClientRange]
}

// $COVERAGE-ON$
