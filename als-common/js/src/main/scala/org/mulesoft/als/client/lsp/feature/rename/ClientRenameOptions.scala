package org.mulesoft.als.client.lsp.feature.rename

import org.mulesoft.lsp.feature.rename.RenameOptions

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import js.JSConverters._

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