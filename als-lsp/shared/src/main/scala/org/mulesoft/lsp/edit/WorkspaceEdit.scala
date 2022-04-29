package org.mulesoft.lsp.edit

/** A workspace edit represents changes to many resources managed in the workspace. The edit should either provide
  * changes or documentChanges. If the client can handle versioned document edits and if documentChanges are present,
  * the latter are preferred over changes.
  *
  * @param changes
  *   Holds changes to existing resources.
  * @param documentChanges
  *   Depending on the client capability `workspace.workspaceEdit.resourceOperations` document changes are either an
  *   array of `TextDocumentEdit`s to express changes to n different text documents where each text document edit
  *   addresses a specific version of a text document. Or it can contain above `TextDocumentEdit`s mixed with create,
  *   rename and delete file / folder operations.
  *
  * Whether a client supports versioned document edits is expressed via `workspace.workspaceEdit.documentChanges` client
  * capability.
  *
  * If a client neither supports `documentChanges` nor `workspace.workspaceEdit.resourceOperations` then only plain
  * `TextEdit`s using the `changes` property are supported.
  */
case class WorkspaceEdit(
    changes: Option[Map[String, Seq[TextEdit]]],
    documentChanges: Option[Seq[Either[TextDocumentEdit, ResourceOperation]]]
)

object WorkspaceEdit {
  val empty: WorkspaceEdit = WorkspaceEdit(Some(Map.empty), None)
}
