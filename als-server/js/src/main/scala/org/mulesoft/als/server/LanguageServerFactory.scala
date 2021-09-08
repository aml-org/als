package org.mulesoft.als.server

import amf.core.client.platform.resource.ClientResourceLoader
import amf.core.client.platform.validation.payload.{AMFPayloadValidationPluginConverter, JsAMFPayloadValidationPlugin}
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import amf.core.internal.convert.PayloadValidationPluginConverter.PayloadValidationPluginMatcher.asInternal
import org.mulesoft.als.configuration.{
  ClientDirectoryResolver,
  DefaultJsServerSystemConf,
  EmptyJsDirectoryResolver,
  JsServerSystemConf
}
import org.mulesoft.als.server.client.{AlsClientNotifier, ClientNotifier}
import org.mulesoft.als.logger.PrintLnLogger
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.custom.AMFOpaValidator
import org.mulesoft.als.server.modules.diagnostic.{DiagnosticNotificationsKind, JsCustomValidator}
import org.mulesoft.als.server.protocol.LanguageServer
import org.yaml.builder.{DocBuilder, JsOutputBuilder}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
import scala.scalajs.js.{Dynamic, UndefOr, isUndefined}

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
                  amfPlugins: js.Array[JsAMFPayloadValidationPlugin] = js.Array.apply()): LanguageServer = {
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
                       plugins: js.Array[JsAMFPayloadValidationPlugin] = js.Array(),
                       logger: js.UndefOr[ClientLogger] = js.undefined,
                       withDiagnostics: Boolean = true,
                       notificationKind: js.UndefOr[DiagnosticNotificationsKind] = js.undefined): LanguageServer = {

    val scalaPlugins: Seq[AMFShapePayloadValidationPlugin] =
      plugins
        .map(AMFPayloadValidationPluginConverter.toAMF)
        .map(asInternal)

    buildServer(clientNotifier, serialization, jsServerSystemConf, logger, notificationKind, scalaPlugins)
  }

  def buildServer(clientNotifier: ClientNotifier,
                  serialization: JsSerializationProps,
                  jsServerSystemConf: JsServerSystemConf,
                  logger: UndefOr[ClientLogger],
                  notificationKind: UndefOr[DiagnosticNotificationsKind],
                  scalaPlugins: Seq[AMFShapePayloadValidationPlugin]) = {
    jsServerSystemConf.amfConfiguration.withValidators(scalaPlugins)
    val factory =
      new WorkspaceManagerFactoryBuilder(clientNotifier, sharedLogger(logger))
        .withAmfConfiguration(jsServerSystemConf.amfConfiguration)
        .withDirectoryResolver(jsServerSystemConf.directoryResolver)

    notificationKind.toOption.foreach(factory.withNotificationKind)

    // TODO: delete after ALS-1606
    val platformValidator: Option[AMFOpaValidator] =
      if (PlatformExplorer.isNode) Some(new JsCustomValidator(sharedLogger(logger))) else None

    val dm                    = factory.buildDiagnosticManagers(platformValidator)
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
        .addRequestModule(sm)
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
        .addRequestModule(builders.workspaceConfigurationManager)
        .addInitializable(builders.telemetryManager)
    dm.foreach(languageBuilder.addInitializableModule)
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
  override def newDocBuilder(prettyPrint: Boolean): DocBuilder[js.Any] =
    JsOutputBuilder() // TODO: JsOutputBuilder with prettyPrint
}

object PlatformExplorer {
  private def isDefined(a: js.Any) = !isUndefined(a)

  /* Return true if js is running on node. */
  def isNode: Boolean =
    if (isDefined(Dynamic.global.process) && isDefined(Dynamic.global.process.versions))
      isDefined(Dynamic.global.process.versions.node)
    else false
}
