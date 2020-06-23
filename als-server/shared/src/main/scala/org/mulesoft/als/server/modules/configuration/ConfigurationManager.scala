package org.mulesoft.als.server.modules.configuration

import org.mulesoft.als.configuration.{AlsConfiguration, AlsConfigurationReader}
import org.mulesoft.als.server.feature.configuration.{
  UpdateConfigurationClientCapabilities,
  UpdateConfigurationConfigType,
  UpdateConfigurationParams,
  UpdateConfigurationServerOptions
}
import org.mulesoft.lsp.{ConfigType, InitializableModule}

import scala.concurrent.Future

class ConfigurationManager
    extends ConfigurationProvider
    with InitializableModule[UpdateConfigurationClientCapabilities, UpdateConfigurationServerOptions] {

  def update(params: UpdateConfigurationParams): Unit = {
    params.updateFormatOptionsParams.foreach(f => {
      configuration.updateFormattingOptions(f)
    })
  }

  def getConfiguration: AlsConfigurationReader = configuration;

  private val configuration: AlsConfiguration = AlsConfiguration();

  override def applyConfig(config: Option[UpdateConfigurationClientCapabilities]): UpdateConfigurationServerOptions = {
    config.foreach(c => configuration.setUpdateFormatOptions(c.enableUpdateFormatOptions))
    UpdateConfigurationServerOptions(configuration.updateFormatOptionsIsEnabled())
  }

  override val `type`: ConfigType[UpdateConfigurationClientCapabilities, UpdateConfigurationServerOptions] =
    UpdateConfigurationConfigType

  override def initialize(): Future[Unit] = { Future.successful() }
}
