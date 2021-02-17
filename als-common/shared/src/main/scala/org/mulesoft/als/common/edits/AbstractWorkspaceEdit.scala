package org.mulesoft.als.common.edits

import org.mulesoft.lsp.edit.{ResourceOperation, TextDocumentEdit, TextEdit, WorkspaceEdit}

case class AbstractWorkspaceEdit(documentChanges: Seq[Either[TextDocumentEdit, ResourceOperation]]) {

  def hasChanges: Boolean = documentChanges.nonEmpty

  def needsDocumentChanges: Boolean = !documentChanges.forall(_.isLeft)

  def asDocumentChanges: WorkspaceEdit = WorkspaceEdit(None, Some(documentChanges))

  def toChanges(documentChanges: Seq[Either[TextDocumentEdit, ResourceOperation]]): Map[String, Seq[TextEdit]] = {
    if (needsDocumentChanges) // in case we want to partially convert, then remove this condition
      throw new Exception("Cannot convert to WorkspaceEdit.changes. Contains a Resource Operation")
    else
      documentChanges
        .collect {
          case Left(value) => value
        }
        .groupBy(_.textDocument.uri)
        .map(t => t._1 -> t._2.flatMap(_.edits))
  }

  def asChanges: WorkspaceEdit = WorkspaceEdit(Some(toChanges(documentChanges)), None)

  def toWorkspaceEdit(supportsDC: Boolean): WorkspaceEdit =
    if (supportsDC) asDocumentChanges
    else asChanges
}

object AbstractWorkspaceEdit {
  val empty: AbstractWorkspaceEdit = AbstractWorkspaceEdit(Seq.empty)
}

//case class WorkspaceEdit(changes: Map[String, Seq[TextEdit]],
//                         documentChanges: Seq[Either[TextDocumentEdit, ResourceOperation]])
