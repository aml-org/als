package org.mulesoft.lsp.feature.folding

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientFoldingRangeCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean] = js.native
  def rangeLimit: js.UndefOr[Int]              = js.native
  def lineFoldingOnly: js.UndefOr[Boolean]     = js.native
}

object ClientFoldingRangeCapabilities {
  def apply(internal: FoldingRangeCapabilities): ClientFoldingRangeCapabilities =
    js.Dynamic
      .literal(
        dynamicRegistration = internal.dynamicRegistration.orUndefined,
        rangeLimit = internal.rangeLimit.orUndefined,
        lineFoldingOnly = internal.lineFoldingOnly.orUndefined
      )
      .asInstanceOf[ClientFoldingRangeCapabilities]
}

// $COVERAGE-ON$
