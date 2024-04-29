package org.mulesoft.lsp.textsync

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.{UndefOr, |}
import org.mulesoft.lsp.convert.LspConvertersSharedToClient.ClientSaveOptionsConverter
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientTextDocumentSyncOptions extends js.Object {
  def openClose: UndefOr[Boolean]                = js.native
  def change: UndefOr[Int]                       = js.native
  def willSave: UndefOr[Boolean]                 = js.native
  def willSaveWaitUntil: UndefOr[Boolean]        = js.native
  def save: UndefOr[ClientSaveOptions | Boolean] = js.native
}

object ClientTextDocumentSyncOptions {
  def apply(internal: TextDocumentSyncOptions): ClientTextDocumentSyncOptions =
    js.Dynamic
      .literal(
        openClose = internal.openClose.orUndefined,
        change = internal.change.map(_.id).orUndefined,
        willSave = internal.willSave.orUndefined,
        willSaveWaitUntil = internal.willSaveWaitUntil.orUndefined,
        save = internal.save.map(_.toClient).orUndefined
      )
      .asInstanceOf[ClientTextDocumentSyncOptions]
}

// $COVERAGE-ON$
