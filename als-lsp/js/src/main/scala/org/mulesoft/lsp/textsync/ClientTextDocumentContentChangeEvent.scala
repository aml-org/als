package org.mulesoft.lsp.textsync

import org.mulesoft.lsp.feature.common.ClientRange

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.UndefOr
import org.mulesoft.lsp.convert.LspConvertersSharedToClient.ClientRangeConverter
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

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

// $COVERAGE-ON$
