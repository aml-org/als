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
import org.mulesoft.als.server.logger.PrintLnLogger
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.DiagnosticNotificationsKind
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.amfintegration.AmfInstance
import org.yaml.builder.{DocBuilder, JsOutputBuilder}

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("LanguageServerFactory")
object LanguageServerFactory {

  def fromLoaders(clientNotifier: ClientNotifier,
                  serializationProps: JsSerializationProps,
                  clientLoaders: js.Array[ClientResourceLoader] = js.Array(),
                  clientDirResolver: ClientDirectoryResolver = EmptyJsDirectoryResolver,
                  logger: js.UndefOr[ClientLogger] = js.undefined,
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
                       serialization: JsSerializationProps,
                       jsServerSystemConf: JsServerSystemConf = DefaultJsServerSystemConf,
                       plugins: js.Array[ClientAMFPayloadValidationPlugin] = js.Array(),
                       logger: js.UndefOr[ClientLogger] = js.undefined,
                       withDiagnostics: Boolean = true,
                       notificationKind: js.UndefOr[DiagnosticNotificationsKind] = js.undefined): LanguageServer = {

    val factory =
      new WorkspaceManagerFactoryBuilder(clientNotifier, sharedLogger(logger), jsServerSystemConf.environment)
        .withAmfConfiguration(
          new AmfInstance(plugins.toSeq.map(ClientPayloadPluginConverter.convert),
                          jsServerSystemConf.platform,
                          jsServerSystemConf.environment))
        .withPlatform(jsServerSystemConf.platform)
        .withDirectoryResolver(jsServerSystemConf.directoryResolver)

    notificationKind.toOption.foreach(factory.withNotificationKind)

    val dm                    = factory.diagnosticManager()
    val sm                    = factory.serializationManager(serialization)
    val filesInProjectManager = factory.filesInProjectManager(serialization.alsClientNotifier)
    val builders              = factory.buildWorkspaceManagerFactory()

    val languageBuilder =
      new LanguageServerBuilder(builders.documentManager,
                                builders.workspaceManager,
                                builders.configurationManager,
                                builders.resolutionTaskManager,
                                sharedLogger(logger))
        .addInitializableModule(sm)
        .addInitializableModule(filesInProjectManager)
        .addInitializable(builders.workspaceManager)
        .addInitializable(builders.resolutionTaskManager)
        .addInitializable(builders.configurationManager)
        .addRequestModule(builders.cleanDiagnosticManager)
        .addRequestModule(builders.conversionManager)
        .addRequestModule(builders.completionManager)
        .addRequestModule(builders.structureManager)
        .addRequestModule(builders.definitionManager)
        .addRequestModule(builders.implementationManager)
        .addRequestModule(builders.typeDefinitionManager)
        .addRequestModule(builders.hoverManager)
        .addRequestModule(builders.referenceManager)
        .addRequestModule(builders.fileUsageManager)
        .addRequestModule(builders.documentLinksManager)
        .addRequestModule(builders.renameManager)
        .addRequestModule(builders.documentHighlightManager)
        .addRequestModule(builders.foldingRangeManager)
        .addRequestModule(builders.selectionRangeManager)
        .addRequestModule(builders.renameFileActionManager)
        .addRequestModule(builders.codeActionManager)
        .addRequestModule(builders.documentFormattingManager)
        .addRequestModule(builders.documentRangeFormattingManager)
        .addInitializable(builders.telemetryManager)
    dm.foreach(languageBuilder.addInitializableModule)
    builders.serializationManager.foreach(languageBuilder.addRequestModule)
    languageBuilder.build()
  }

  private def sharedLogger(logger: UndefOr[ClientLogger]) = {
    logger.toOption.map(l => ClientLoggerAdapter(l)).getOrElse(PrintLnLogger)
  }
}

@JSExportAll
@JSExportTopLevel("JsSerializationProps")
case class JsSerializationProps(override val alsClientNotifier: AlsClientNotifier[js.Any])
    extends SerializationProps[js.Any](alsClientNotifier) {
  override def newDocBuilder(): DocBuilder[js.Any] = JsOutputBuilder()
}
