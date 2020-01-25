package org.mulesoft.als.server

import amf.client.convert.ClientPayloadPluginConverter
import amf.client.plugins.ClientAMFPayloadValidationPlugin
import amf.client.resource.ClientResourceLoader
import org.mulesoft.als.client.configuration._
import org.mulesoft.als.server.client.{AlsClientNotifier, ClientNotifier}
import org.mulesoft.als.server.logger.{Logger, PrintLnLogger}
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.DiagnosticNotificationsKind
import org.mulesoft.lsp.server.{AmfInstance, LanguageServer}
import org.yaml.builder.{DocBuilder, JsOutputBuilder}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("LanguageServerFactory")
object LanguageServerFactory {

  def fromLoaders(clientNotifier: ClientNotifier with AlsClientNotifier[js.Any],
                  clientLoaders: js.Array[ClientResourceLoader] = js.Array(),
                  clientDirResolver: ClientDirectoryResolver = EmptyJsDirectoryResolver,
                  logger: Logger = PrintLnLogger,
                  withDiagnostics: Boolean = true,
                  notificationKind: Option[DiagnosticNotificationsKind] = None,
                  amfPlugins: js.Array[ClientAMFPayloadValidationPlugin] = js.Array.apply()): LanguageServer = {
    fromSystemConfig(clientNotifier,
                     JsServerSystemConf(clientLoaders, clientDirResolver),
                     amfPlugins,
                     logger,
                     withDiagnostics,
                     notificationKind)
  }

  def fromSystemConfig(clientNotifier: ClientNotifier with AlsClientNotifier[js.Any],
                       jsServerSystemConf: JsServerSystemConf = DefaultJsServerSystemConf,
                       plugins: js.Array[ClientAMFPayloadValidationPlugin] = js.Array(),
                       logger: Logger = PrintLnLogger,
                       withDiagnostics: Boolean = true,
                       notificationKind: Option[DiagnosticNotificationsKind] = None): LanguageServer = {

    val builders = new WorkspaceManagerFactoryBuilder(clientNotifier, logger)
      .withAmfConfiguration(
        new AmfInstance(plugins.toSeq.map(ClientPayloadPluginConverter.convert),
                        jsServerSystemConf.platform,
                        jsServerSystemConf.environment))
    val dm = builders.diagnosticManager()
    val sm = builders.serializationManager(JsSerializationProps(clientNotifier))

    notificationKind.foreach(builders.withNotificationKind)
    val factory = builders.buildWorkspaceManagerFactory()
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager)
      .addInitializableModule(dm)
      .addInitializableModule(sm)
      .addInitializableModule(fit)
      .addInitializable(factory.cleanDiagnosticManager)
      .addInitializable(factory.workspaceManager)
      .addRequestModule(factory.completionManager)
      .addRequestModule(factory.structureManager)
      .addRequestModule(factory.definitionManager)
      .addRequestModule(factory.referenceManager)
      .addRequestModule(factory.documentLinksManager)
      .addInitializable(factory.telemetryManager)
      .build()
  }
}

case class JsSerializationProps(override val alsClientNotifier: AlsClientNotifier[js.Any])
    extends SerializationProps[js.Any](alsClientNotifier) {
  override def newDocBuilder(): DocBuilder[js.Any] = JsOutputBuilder()
}
