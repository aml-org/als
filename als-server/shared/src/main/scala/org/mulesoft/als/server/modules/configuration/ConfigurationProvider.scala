package org.mulesoft.als.server.modules.configuration

import org.mulesoft.als.configuration.AlsConfigurationReader

trait ConfigurationProvider {
  def getConfiguration: AlsConfigurationReader
  def getHotReloadDialects: Boolean
  def getMaxFileSize: Option[Int]
  def getShouldRetryExternalFragments: Boolean
}
