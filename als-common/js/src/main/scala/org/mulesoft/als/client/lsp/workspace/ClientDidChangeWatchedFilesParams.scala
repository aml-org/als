package org.mulesoft.als.client.lsp.workspace

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.workspace.DidChangeWatchedFilesParams

@js.native
trait ClientDidChangeWatchedFilesParams extends js.Object {
  def changes: js.Array[ClientFileEvent] = js.native
}

object ClientDidChangeWatchedFilesParams {
  def apply(internal: DidChangeWatchedFilesParams): ClientDidChangeWatchedFilesParams =
    js.Dynamic
      .literal(changes = internal.changes.map(_.toClient).toJSArray)
      .asInstanceOf[ClientDidChangeWatchedFilesParams]
}
