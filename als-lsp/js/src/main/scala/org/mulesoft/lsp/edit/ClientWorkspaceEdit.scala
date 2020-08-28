package org.mulesoft.lsp.edit

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.|
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientWorkspaceEdit extends js.Object {
  def changes: js.Dictionary[js.Array[ClientTextEdit]]                            = js.native
  def documentChanges: js.Array[ClientTextDocumentEdit | ClientResourceOperation] = js.native
}

object ClientWorkspaceEdit {
  def apply(internal: WorkspaceEdit): ClientWorkspaceEdit = {
    val edits = internal.documentChanges.collect {
      case Left(v)  => v.toClient
      case Right(v) => v.toClient
    }

    js.Dynamic
      .literal(
        changes = internal.changes.mapValues(s => s.map(_.toClient).toJSArray).toJSDictionary,
        documentChanges = edits.toJSArray
      )
      .asInstanceOf[ClientWorkspaceEdit]
  }
}
// $COVERAGE-ON$
