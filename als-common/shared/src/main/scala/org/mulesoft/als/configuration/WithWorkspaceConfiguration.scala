package org.mulesoft.als.configuration

trait WithWorkspaceConfiguration {
  private var workspaceConfig: Option[WorkspaceConfiguration] = None

  def withWorkspaceConfiguration(workspaceConfiguration: WorkspaceConfiguration): Unit =
    workspaceConfig = Some(workspaceConfiguration)

  def workspaceConfiguration: Option[WorkspaceConfiguration] = workspaceConfiguration
}
