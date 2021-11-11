package org.mulesoft.als.configuration

trait WithProjectConfiguration {
  private var projectConfig: Option[ProjectConfiguration] = None

  def withProjectConfiguration(projectConfiguration: ProjectConfiguration): this.type = {
    projectConfig = Some(projectConfiguration)
    this
  }

  def withProjectConfiguration(projectConfiguration: Option[ProjectConfiguration]): this.type = {
    projectConfig = projectConfiguration
    this
  }

  def projectConfiguration: Option[ProjectConfiguration] = projectConfig
}
