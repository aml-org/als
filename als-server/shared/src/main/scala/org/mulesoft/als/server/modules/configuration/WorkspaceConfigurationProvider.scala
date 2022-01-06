package org.mulesoft.als.server.modules.configuration

import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.server.modules.workspace.WorkspaceContentManager
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState

import scala.concurrent.Future

trait WorkspaceConfigurationProvider {
  def getWorkspaceConfiguration(uri: String): Future[(WorkspaceContentManager, ProjectConfiguration)]
  def getConfigurationState(uri: String): Future[ALSConfigurationState]
}
