package org.mulesoft.lsp.feature.selection

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.lsp.feature.common.{ClientPosition, ClientTextDocumentIdentifier}
import org.mulesoft.lsp.feature.selectionRange.SelectionRangeParams
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientSelectionRangeParams extends js.Object {
  def textDocument: ClientTextDocumentIdentifier = js.native
  def positions: js.Array[ClientPosition]        = js.native
}

object ClientSelectionRangeParams {
  def apply(internal: SelectionRangeParams): ClientSelectionRangeParams =
    js.Dynamic
      .literal(
        textDocument = internal.textDocument.toClient,
        positions = internal.positions.map(_.toClient).toJSArray
      )
      .asInstanceOf[ClientSelectionRangeParams]
}

// $COVERAGE-ON$
