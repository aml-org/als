package org.mulesoft.lsp.configuration

case class WorkspaceServerCapabilities(workspaceFolders: Option[WorkspaceFolderServerCapabilities])

class WorkspaceServerCapabilitiesBuilder {
  private var current: WorkspaceServerCapabilities                                         = WorkspaceServerCapabilities(None)
  private var workspaceFolderServerCapabilities: Option[WorkspaceFolderServerCapabilities] = None

  def withWorkspaceFolderSupport: WorkspaceServerCapabilitiesBuilder = {

    workspaceFolderServerCapabilities match {
      case Some(_) => workspaceFolderServerCapabilities = workspaceFolderServerCapabilities.map(_.withSupported(true))
      case None    => workspaceFolderServerCapabilities = Some(WorkspaceFolderServerCapabilities(Some(true), None))
    }
    current = WorkspaceServerCapabilities(workspaceFolderServerCapabilities)
    this
  }

  def withChangeNotification(value: Either[String, Boolean]): WorkspaceServerCapabilitiesBuilder = {
    workspaceFolderServerCapabilities match {
      case Some(_) =>
        workspaceFolderServerCapabilities = workspaceFolderServerCapabilities.map(_.withChangeNotification(value))
      case None => workspaceFolderServerCapabilities = Some(WorkspaceFolderServerCapabilities(None, Some(value)))
    }
    current = WorkspaceServerCapabilities(workspaceFolderServerCapabilities)
    this
  }

  def build: WorkspaceServerCapabilities = current
}
