package org.mulesoft.als.client.lsp.edit

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.edit.{TextDocumentEdit, WorkspaceEdit}
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientWorkspaceEdit extends js.Object {
  def changes: js.Dictionary[js.Array[ClientTextEdit]]  = js.native
  def documentChanges: js.Array[ClientTextDocumentEdit] = js.native
  // TODO: check if it matches spec, should it be Either[Seq[CTDE], Seq[CRO]]?
  // documentChanges?: (TextDocumentEdit[] | (TextDocumentEdit | CreateFile | RenameFile | DeleteFile)[]);
}

object ClientWorkspaceEdit {
  def apply(internal: WorkspaceEdit): ClientWorkspaceEdit = {
    val edits = internal.documentChanges.collect { case Left(v) => v }

    js.Dynamic
      .literal(
        changes = internal.changes.mapValues(s => s.map(_.toClient).toJSArray).toJSDictionary,
        documentChanges = edits.map(_.toClient).toJSArray
      )
      .asInstanceOf[ClientWorkspaceEdit]
  }
}

// @js.native
// trait ClientWorkspaceEditWithResourceOperation extends js.Object {
//  def changes: js.Dictionary[js.Array[ClientTextEdit]] = js.native
//  def documentChanges: js.Array[ClientResourceOperation] = js.native
// }

// $COVERAGE-ON$