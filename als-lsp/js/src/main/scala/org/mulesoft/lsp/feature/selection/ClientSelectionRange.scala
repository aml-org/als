package org.mulesoft.lsp.feature.selection

import org.mulesoft.lsp.feature.common.ClientRange
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.convert.LspConvertersClientToShared._
import org.mulesoft.lsp.feature.selectionRange.SelectionRange
import scala.scalajs.js.JSConverters._

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientSelectionRange extends js.Object {
  def range: ClientRange                       = js.native
  def parent: js.UndefOr[ClientSelectionRange] = js.native // nullable
}

object ClientSelectionRange {
  def apply(internal: SelectionRange): ClientSelectionRange =
    js.Dynamic
      .literal(
        range = internal.range.toClient,
        parent = internal.parent.map(_.toClient).orUndefined
      )
      .asInstanceOf[ClientSelectionRange]
}

// $COVERAGE-ON$
