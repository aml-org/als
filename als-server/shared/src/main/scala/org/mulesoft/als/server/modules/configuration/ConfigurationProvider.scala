package org.mulesoft.als.server.modules.configuration

import org.mulesoft.als.configuration.{AlsConfigurationReader, ProjectConfigurationStyle}

trait ConfigurationProvider {
  def getConfiguration: AlsConfigurationReader
  def getProjectConfigStyle: ProjectConfigurationStyle
  def getHotReloadDialects: Boolean
}
