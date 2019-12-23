package org.mulesoft.als.client.lsp.edit

import scala.scalajs.js

@js.native
trait ClientWorkspaceEdit extends js.Object {
  def changes: js.Dictionary[js.Array[ClientTextEdit]]  = js.native
  def documentChanges: js.Array[ClientTextDocumentEdit] = js.native
  // TODO: check if it matches spec, should it be Either[Seq[CTDE], Seq[CRO]]?
  // documentChanges?: (TextDocumentEdit[] | (TextDocumentEdit | CreateFile | RenameFile | DeleteFile)[]);
}

// @js.native
// trait ClientWorkspaceEditWithResourceOperation extends js.Object {
//  def changes: js.Dictionary[js.Array[ClientTextEdit]] = js.native
//  def documentChanges: js.Array[ClientResourceOperation] = js.native
// }
