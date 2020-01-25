package org.mulesoft.als.client.lsp.textsync

import org.mulesoft.lsp.textsync.SaveOptions

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.UndefOr
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientSaveOptions extends js.Object {
  def includeText: UndefOr[Boolean] = js.native
}

object ClientSaveOptions {
  def apply(internal: SaveOptions): ClientSaveOptions =
    js.Dynamic
      .literal(includeText = internal.includeText.orUndefined)
      .asInstanceOf[ClientSaveOptions]
}

// $COVERAGE-ON$