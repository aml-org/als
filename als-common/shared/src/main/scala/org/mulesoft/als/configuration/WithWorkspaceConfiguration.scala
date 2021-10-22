package org.mulesoft.als.configuration

trait WithWorkspaceConfiguration {
  private var workspaceConfig: Option[WorkspaceConfiguration] = None

  def withWorkspaceConfiguration(workspaceConfiguration: WorkspaceConfiguration): this.type = {
    workspaceConfig = Some(workspaceConfiguration)
    this
  }

  def withWorkspaceConfiguration(workspaceConfiguration: Option[WorkspaceConfiguration]): this.type = {
    workspaceConfig = workspaceConfiguration
    this
  }

  def workspaceConfiguration: Option[WorkspaceConfiguration] = workspaceConfig
}
