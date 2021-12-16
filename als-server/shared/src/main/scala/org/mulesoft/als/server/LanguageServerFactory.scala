package org.mulesoft.als.server

import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.logger.{Logger, PrintLnLogger}
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.custom.AMFOpaValidatorBuilder
import org.mulesoft.als.server.modules.diagnostic.{DiagnosticNotificationsKind, PARSING_BEFORE}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration

import scala.concurrent.ExecutionContext.Implicits.global

class LanguageServerFactory(clientNotifier: ClientNotifier) {
  private var serialization: SerializationProps[_]               = new EmptySerializationProps
  private var logger: Logger                                     = PrintLnLogger
  private var notificationsKind: DiagnosticNotificationsKind     = PARSING_BEFORE
  private var directoryResolver: Option[DirectoryResolver]       = None
  private var rl: Seq[ResourceLoader]                            = Seq.empty
  private var plugins: Seq[AMFShapePayloadValidationPlugin]      = Seq.empty
  private var amfCustomValidatorBuilder: Option[AMFOpaValidatorBuilder]        = None

  def withSerializationProps(serializationProps: SerializationProps[_]): this.type = {
    serialization = serializationProps
    this
  }

  def withResourceLoaders(rl: Seq[ResourceLoader]): this.type = {
    this.rl = rl
    this
  }

  def withLogger(logger: Logger): this.type = {
    this.logger = logger
    this
  }

  def withNotificationKind(notificationsKind: DiagnosticNotificationsKind): this.type = {
    this.notificationsKind = notificationsKind
    this
  }

  def withDirectoryResolver(dr: DirectoryResolver): this.type = {
    this.directoryResolver = Some(dr)
    this
  }

  def withAmfPlugins(plugin: Seq[AMFShapePayloadValidationPlugin]): this.type = {
    plugins = plugin
    this
  }

  def withAmfCustomValidator(validator: AMFOpaValidatorBuilder): this.type = {
    amfCustomValidatorBuilder = Some(validator)
    this
  }

  def build(): LanguageServer = {
    val resourceLoaders     = if (rl.isEmpty) EditorConfiguration.platform.loaders() else rl
    val editorConfiguration = new EditorConfiguration(resourceLoaders, Seq.empty, plugins, logger)
    val factory             = new WorkspaceManagerFactoryBuilder(clientNotifier, logger, editorConfiguration)

    directoryResolver.foreach(cdr => factory.withDirectoryResolver(cdr))
    factory.withNotificationKind(notificationsKind) // move to initialization param
    val dm                    = factory.buildDiagnosticManagers(amfCustomValidatorBuilder.map(_.build(logger)))
    val sm                    = factory.serializationManager(serialization)
    val filesInProjectManager = factory.filesInProjectManager(serialization.alsClientNotifier)
    val builders              = factory.buildWorkspaceManagerFactory()

    val languageBuilder =
      new LanguageServerBuilder(builders.documentManager,
        builders.workspaceManager,
        builders.configurationManager,
        builders.resolutionTaskManager,
        logger)
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
        .addRequestModule(builders.workspaceConfigurationManager)
        .addInitializable(builders.telemetryManager)
    dm.foreach(m => languageBuilder.addInitializableModule(m))
    builders.serializationManager.foreach(languageBuilder.addRequestModule)
    languageBuilder.build()
  }
}
