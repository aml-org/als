package org.mulesoft.lsp.configuration

case class WorkspaceServerCapabilities(workspaceFolders: Option[WorkspaceFolderServerCapabilities])

object DefaultWorkspaceServerCapabilities
    extends WorkspaceServerCapabilities(Some(WorkspaceFolderServerCapabilities(Some(true), None)))
