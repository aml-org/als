package org.mulesoft.lsp.edit

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.|
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientWorkspaceEdit extends js.Object {
  def changes: js.UndefOr[js.Dictionary[js.Array[ClientTextEdit]]]                            = js.native
  def documentChanges: js.UndefOr[js.Array[ClientTextDocumentEdit | ClientResourceOperation]] = js.native
}

object ClientWorkspaceEdit {
  def apply(internal: WorkspaceEdit): ClientWorkspaceEdit = {
    val edits = internal.documentChanges
      .map(_.collect {
        case Left(v)  => v.toClient
        case Right(v) => v.toClient
      })
      .orUndefined

    js.Dynamic
      .literal(
        changes = internal.changes.map(_.mapValues(s => s.map(_.toClient).toJSArray).toJSDictionary).orUndefined,
        documentChanges = edits.map(_.toJSArray)
      )
      .asInstanceOf[ClientWorkspaceEdit]
  }
}
// $COVERAGE-ON$
