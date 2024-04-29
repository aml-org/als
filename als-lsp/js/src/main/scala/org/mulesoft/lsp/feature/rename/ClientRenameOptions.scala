package org.mulesoft.lsp.feature.rename

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientRenameOptions extends js.Object {
  def prepareProvider: UndefOr[Boolean] = js.native
}

object ClientRenameOptions {
  def apply(internal: RenameOptions): ClientRenameOptions =
    js.Dynamic
      .literal(prepareProvider = internal.prepareProvider.orUndefined)
      .asInstanceOf[ClientRenameOptions]
}
// $COVERAGE-ON$
