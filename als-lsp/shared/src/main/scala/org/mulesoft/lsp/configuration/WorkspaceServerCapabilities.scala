package org.mulesoft.lsp.configuration

case class WorkspaceServerCapabilities(workspaceFolders: Option[WorkspaceFolderServerCapabilities])

object CurrentWorkspaceServerCapabilities
    extends WorkspaceServerCapabilities(Some(WorkspaceFolderServerCapabilities(Some(true), Option(Right(true)))))
