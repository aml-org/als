package org.mulesoft.als.server.modules.configuration

import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.server.modules.workspace.WorkspaceContentManager

import scala.concurrent.Future

trait WorkspaceConfigurationProvider {
  def getWorkspaceConfiguration(uri: String): Future[(WorkspaceContentManager, Option[ProjectConfiguration])]
}
