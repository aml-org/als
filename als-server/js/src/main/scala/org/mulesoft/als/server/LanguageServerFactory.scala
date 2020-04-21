package org.mulesoft.als.server

import amf.client.convert.ClientPayloadPluginConverter
import amf.client.plugins.ClientAMFPayloadValidationPlugin
import amf.client.resource.ClientResourceLoader
import org.mulesoft.als.configuration.{
  ClientDirectoryResolver,
  DefaultJsServerSystemConf,
  EmptyJsDirectoryResolver,
  JsServerSystemConf
}
import org.mulesoft.als.server.client.{AlsClientNotifier, ClientNotifier}
import org.mulesoft.als.server.logger.{Logger, PrintLnLogger}
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.DiagnosticNotificationsKind
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.amfintegration.AmfInstance
import org.yaml.builder.{DocBuilder, JsOutputBuilder}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("LanguageServerFactory")
object LanguageServerFactory {

  def fromLoaders(clientNotifier: ClientNotifier,
                  serializationProps: JsSerializationProps,
                  clientLoaders: js.Array[ClientResourceLoader] = js.Array(),
                  clientDirResolver: ClientDirectoryResolver = EmptyJsDirectoryResolver,
                  logger: Logger = PrintLnLogger,
                  withDiagnostics: Boolean = true,
                  notificationKind: js.UndefOr[DiagnosticNotificationsKind] = js.undefined,
                  amfPlugins: js.Array[ClientAMFPayloadValidationPlugin] = js.Array.apply()): LanguageServer = {
    fromSystemConfig(clientNotifier,
                     serializationProps,
                     JsServerSystemConf(clientLoaders, clientDirResolver),
                     amfPlugins,
                     logger,
                     withDiagnostics,
                     notificationKind)
  }

  def fromSystemConfig(clientNotifier: ClientNotifier,
                       serializationProps: JsSerializationProps,
                       jsServerSystemConf: JsServerSystemConf = DefaultJsServerSystemConf,
                       plugins: js.Array[ClientAMFPayloadValidationPlugin] = js.Array(),
                       logger: Logger = PrintLnLogger,
                       withDiagnostics: Boolean = true,
                       notificationKind: js.UndefOr[DiagnosticNotificationsKind] = js.undefined): LanguageServer = {

    val builders = new WorkspaceManagerFactoryBuilder(clientNotifier, logger, jsServerSystemConf.environment)
      .withAmfConfiguration(
        new AmfInstance(plugins.toSeq.map(ClientPayloadPluginConverter.convert),
                        jsServerSystemConf.platform,
                        jsServerSystemConf.environment))
      .withPlatform(jsServerSystemConf.platform)
      .withDirectoryResolver(jsServerSystemConf.directoryResolver)

    notificationKind.toOption.foreach(builders.withNotificationKind)

    val diagnosticManager     = builders.diagnosticManager()
    val filesInProjectManager = builders.filesInProjectManager(serializationProps.alsClientNotifier)
    val serializationManager  = builders.serializationManager(serializationProps)

    val factory = builders.buildWorkspaceManagerFactory()
    val builder =
      new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, factory.resolutionTaskManager)
        .addInitializableModule(serializationManager)
        .addInitializableModule(filesInProjectManager)
        .addInitializable(factory.cleanDiagnosticManager)
        .addInitializable(factory.workspaceManager)
        .addRequestModule(factory.cleanDiagnosticManager)
        .addRequestModule(factory.completionManager)
        .addRequestModule(factory.conversionManager)
        .addRequestModule(factory.structureManager)
        .addRequestModule(factory.definitionManager)
        .addRequestModule(factory.referenceManager)
        .addRequestModule(factory.documentLinksManager)
        .addInitializable(factory.telemetryManager)
    diagnosticManager.foreach(builder.addInitializableModule)
    factory.serializationManager.foreach(builder.addRequestModule)
    builder
      .build()
  }
}

@JSExportAll
@JSExportTopLevel("JsSerializationProps")
case class JsSerializationProps(override val alsClientNotifier: AlsClientNotifier[js.Any])
    extends SerializationProps[js.Any](alsClientNotifier) {
  override def newDocBuilder(): DocBuilder[js.Any] = JsOutputBuilder()
}
