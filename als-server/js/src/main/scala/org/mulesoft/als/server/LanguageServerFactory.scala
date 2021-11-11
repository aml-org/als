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
import org.mulesoft.als.logger.PrintLnLogger
import org.mulesoft.als.server.client.{AlsClientNotifier, ClientNotifier}
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.custom.AMFOpaValidator
import org.mulesoft.als.server.modules.diagnostic.{DiagnosticNotificationsKind, JsCustomValidator}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.wasm.{AmfCustomValidatorWeb, AmfWasmOpaValidator}
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.yaml.builder.{DocBuilder, JsOutputBuilder}

import scala.concurrent.ExecutionContext.Implicits.global
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
                  notificationKind: js.UndefOr[DiagnosticNotificationsKind] = js.undefined,
                  amfPlugins: js.Array[JsAMFPayloadValidationPlugin] = js.Array.apply(),
                  amfCustomValidator: AmfWasmOpaValidator = AmfCustomValidatorWeb): LanguageServer = {
    fromSystemConfig(
      clientNotifier,
      serializationProps,
      JsServerSystemConf(clientLoaders, clientDirResolver),
      amfPlugins,
      logger,
      notificationKind,
      amfCustomValidator
    )
  }

  def fromSystemConfig(clientNotifier: ClientNotifier,
                       serialization: JsSerializationProps,
                       jsServerSystemConf: JsServerSystemConf = DefaultJsServerSystemConf,
                       plugins: js.Array[JsAMFPayloadValidationPlugin] = js.Array(),
                       logger: js.UndefOr[ClientLogger] = js.undefined,
                       notificationKind: js.UndefOr[DiagnosticNotificationsKind] = js.undefined,
                       amfCustomValidator: AmfWasmOpaValidator = AmfCustomValidatorWeb): LanguageServer = {

    val scalaPlugins: Seq[AMFShapePayloadValidationPlugin] =
      plugins
        .map(AMFPayloadValidationPluginConverter.toAMF)
        .map(asInternal)

    buildServer(clientNotifier,
                serialization,
                jsServerSystemConf,
                logger,
                notificationKind,
                scalaPlugins,
                Some(amfCustomValidator))
  }

  def buildServer(clientNotifier: ClientNotifier,
                  serialization: JsSerializationProps,
                  jsServerSystemConf: JsServerSystemConf,
                  logger: UndefOr[ClientLogger],
                  notificationKind: UndefOr[DiagnosticNotificationsKind],
                  scalaPlugins: Seq[AMFShapePayloadValidationPlugin],
                  amfCustomValidator: Option[AmfWasmOpaValidator]) = {

    val globalProjectConfiguration: EditorConfiguration =
      EditorConfiguration(jsServerSystemConf.loaders, Seq.empty, scalaPlugins, sharedLogger(logger))

    val factory =
      new WorkspaceManagerFactoryBuilder(clientNotifier, sharedLogger(logger), globalProjectConfiguration)
        .withDirectoryResolver(jsServerSystemConf.directoryResolver)

    notificationKind.toOption.foreach(factory.withNotificationKind)

    val platformValidator: Option[AMFOpaValidator] =
      amfCustomValidator.map(JsCustomValidator(sharedLogger(logger), _))

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
    dm.foreach(m => languageBuilder.addInitializableModule(m))
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
