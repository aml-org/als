package org.mulesoft.lsp.feature.rename

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.common.ClientRange

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientPrepareRenameDefaultBehavior extends js.Object {
  def range: ClientRange       = js.native
  def placeholder: String      = js.native
  def defaultBehavior: Boolean = js.native
}

object ClientPrepareRenameDefaultBehavior {
  def apply(internal: PrepareRenameDefaultBehavior): ClientPrepareRenameDefaultBehavior =
    js.Dynamic
      .literal(
        range = internal.range.toClient,
        placeholder = internal.placeholder,
        defaultBehavior = internal.defaultBehavior
      )
      .asInstanceOf[ClientPrepareRenameDefaultBehavior]
}

// $COVERAGE-ON$
