package org.mulesoft.als.client.lsp.textsync

import scala.scalajs.js
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.common.ClientRange
import org.mulesoft.lsp.textsync.TextDocumentContentChangeEvent

import scala.scalajs.js.UndefOr
import js.JSConverters._

@js.native
trait ClientTextDocumentContentChangeEvent extends js.Object {
  def text: String                = js.native
  def range: UndefOr[ClientRange] = js.native
  def rangeLength: UndefOr[Int]   = js.native
}

object ClientTextDocumentContentChangeEvent {
  def apply(internal: TextDocumentContentChangeEvent): ClientTextDocumentContentChangeEvent =
    js.Dynamic
      .literal(
        text = internal.text,
        range = internal.range.map(_.toClient).orUndefined,
        rangeLength = internal.rangeLength.orUndefined
      )
      .asInstanceOf[ClientTextDocumentContentChangeEvent]
}
