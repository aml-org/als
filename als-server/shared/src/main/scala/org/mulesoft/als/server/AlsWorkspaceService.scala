package org.mulesoft.als.server

import org.mulesoft.lsp.configuration.WorkspaceFolder
import org.mulesoft.lsp.workspace.WorkspaceService

import scala.concurrent.Future

trait AlsWorkspaceService extends WorkspaceService {
  def initialize(workspaceFolders: List[WorkspaceFolder]): Future[Unit]
}
