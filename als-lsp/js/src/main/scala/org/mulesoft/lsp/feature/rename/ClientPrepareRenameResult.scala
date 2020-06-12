package org.mulesoft.lsp.feature.rename

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.common.ClientRange

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientPrepareRenameResult extends js.Object {
  def range: ClientRange  = js.native
  def placeholder: String = js.native
}

object ClientPrepareRenameResult {
  def apply(internal: PrepareRenameResult): ClientPrepareRenameResult =
    js.Dynamic
      .literal(
        range = internal.range.toClient,
        placeholder = internal.placeholder
      )
      .asInstanceOf[ClientPrepareRenameResult]
}

// $COVERAGE-ON$
