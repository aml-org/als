package org.mulesoft.lsp.workspace

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

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

// $COVERAGE-ON$
