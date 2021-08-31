package org.mulesoft.als.server.modules.configuration

import org.mulesoft.als.configuration.ConfigurationStyle.ConfigurationStyle
import org.mulesoft.als.configuration.{
  AlsConfiguration,
  AlsConfigurationReader,
  ConfigurationStyle,
  DefaultProjectConfigurationStyle,
  ProjectConfigurationStyle
}
import org.mulesoft.als.server.feature.configuration._
import org.mulesoft.amfintegration.AlsSyamlSyntaxPluginHacked
import org.mulesoft.lsp.InitializableModule

import scala.concurrent.Future

class ConfigurationManager
    extends ConfigurationProvider
    with InitializableModule[UpdateConfigurationClientCapabilities, UpdateConfigurationServerOptions] {

  override val `type`: UpdateConfigurationConfigType.type =
    UpdateConfigurationConfigType

  def update(params: UpdateConfigurationParams): Unit = { // todo: is this not a Request?
    params.updateFormatOptionsParams.foreach(f => {
      configuration.updateFormattingOptions(f)
      configuration.setTemplateType(params.templateType)
    })
    // Should move to separated GenericOptions class?
    params.genericOptions.get(GenericOptionKeys.KeepTokens) match {
      case Some(b: Boolean) => AlsSyamlSyntaxPluginHacked.withKeepTokens(b)
      case _                => // ignore
    }
    configuration.setShouldPrettyPrintSerialization(params.prettyPrintSerialization)
  }

  private var projectConfigurationStyle: ProjectConfigurationStyle = DefaultProjectConfigurationStyle

  def getProjectConfigStyle: ProjectConfigurationStyle = projectConfigurationStyle

  /**
    * Should only be called from initialization
    */
  def setProjectConfigurationStyle(p: ProjectConfigurationStyle): Unit = projectConfigurationStyle = p

  def updateDocumentChangesSupport(support: Boolean): Unit = configuration.supportsDocumentChanges(support)

  def getConfiguration: AlsConfigurationReader = configuration

  private val configuration: AlsConfiguration = AlsConfiguration()

  override def applyConfig(config: Option[UpdateConfigurationClientCapabilities]): UpdateConfigurationServerOptions = {
    config.foreach { c =>
      configuration.setUpdateFormatOptions(c.enableUpdateFormatOptions)
      configuration.supportsDocumentChanges(c.supportsDocumentChanges)
    }
    UpdateConfigurationServerOptions(configuration.updateFormatOptionsIsEnabled())
  }

  override def initialize(): Future[Unit] = { Future.successful() }
}
