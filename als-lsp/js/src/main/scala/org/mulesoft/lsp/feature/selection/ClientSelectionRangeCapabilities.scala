package org.mulesoft.lsp.feature.selection

import org.mulesoft.lsp.feature.selectionRange.SelectionRangeCapabilities
import scala.scalajs.js.JSConverters._

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientSelectionRangeCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean] = js.native
}

object ClientSelectionRangeCapabilities {
  def apply(internal: SelectionRangeCapabilities): ClientSelectionRangeCapabilities =
    js.Dynamic
      .literal(
        dynamicRegistration = internal.dynamicRegistration.orUndefined
      )
      .asInstanceOf[ClientSelectionRangeCapabilities]
}
// $COVERAGE-ON$
