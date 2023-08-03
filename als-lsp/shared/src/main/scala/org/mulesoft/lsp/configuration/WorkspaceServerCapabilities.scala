package org.mulesoft.lsp.configuration

case class WorkspaceServerCapabilities(
    workspaceFolders: Option[WorkspaceFolderServerCapabilities],
    fileOperations: Option[FileOperationsServerCapabilities]
)

object DefaultWorkspaceServerCapabilities
    extends WorkspaceServerCapabilities(
      Some(WorkspaceFolderServerCapabilities(Some(true), None)),
      Some(FileOperationsServerCapabilities.default)
    )
