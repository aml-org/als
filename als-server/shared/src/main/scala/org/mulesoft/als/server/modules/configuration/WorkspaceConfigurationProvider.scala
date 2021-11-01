package org.mulesoft.als.server.modules.configuration

import org.mulesoft.als.server.modules.workspace.WorkspaceContentManager
import org.mulesoft.als.server.workspace.extract.WorkspaceConfig

import scala.concurrent.Future

trait WorkspaceConfigurationProvider {
  def getWorkspaceConfiguration(uri: String): Future[(WorkspaceContentManager, Option[WorkspaceConfig])]
}
