package org.mulesoft.als.client.lsp.common

import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.common.Range

import scala.scalajs.js

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
