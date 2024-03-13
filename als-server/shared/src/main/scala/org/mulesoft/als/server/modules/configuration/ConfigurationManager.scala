package org.mulesoft.als.server.modules.configuration

import org.mulesoft.als.configuration.{AlsConfiguration, AlsConfigurationReader}
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

  private var hotReloadDialects: Boolean = false

  override def getHotReloadDialects: Boolean = hotReloadDialects

  /** Should only be called from initialization
    */
  def setHotReloadDialects(p: Boolean): Unit = hotReloadDialects = p

  private var maxFileSize: Option[Int] = None

  override def getMaxFileSize: Option[Int] = maxFileSize

  /** Should only be called from initialization
    */
  def setMaxFileSize(p: Option[Int]): Unit = maxFileSize = p

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
