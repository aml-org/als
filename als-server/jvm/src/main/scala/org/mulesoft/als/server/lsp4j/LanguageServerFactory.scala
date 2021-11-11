package org.mulesoft.als.server.lsp4j

import amf.core.client.platform.resource.ClientResourceLoader
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import amf.core.internal.convert.CoreClientConverters._
import amf.core.internal.remote.Platform
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.configuration.{ClientDirectoryResolver, DirectoryResolverAdapter}
import org.mulesoft.als.logger.{Logger, PrintLnLogger}
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.{DiagnosticNotificationsKind, PARSING_BEFORE}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{EmptyJvmSerializationProps, JvmSerializationProps, LanguageServerBuilder}
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

// todo: standarize in one only converter (js and jvm) with generics
class LanguageServerFactory(clientNotifier: ClientNotifier) extends PlatformSecrets {
  private var serialization: JvmSerializationProps               = EmptyJvmSerializationProps
  private var logger: Logger                                     = PrintLnLogger
  private var givenPlatform: Platform                            = platform
  private var notificationsKind: DiagnosticNotificationsKind     = PARSING_BEFORE
  private var directoryResolver: Option[ClientDirectoryResolver] = None
  private var rl: Seq[ResourceLoader]                            = Seq.empty
  private var plugins: Seq[AMFShapePayloadValidationPlugin]      = Seq.empty

  def withSerializationProps(serializationProps: JvmSerializationProps): LanguageServerFactory = {
    serialization = serializationProps
    this
  }

  def withResourceLoaders(rl: java.util.List[ClientResourceLoader]): LanguageServerFactory = {
    this.rl = rl.asScala.map(rl => ResourceLoaderMatcher.asInternal(rl)(ExecutionContext.Implicits.global))
    this
  }

  def withLogger(logger: Logger): LanguageServerFactory = {
    this.logger = logger
    this
  }

  def withGivenPlatform(givenPlatform: Platform): LanguageServerFactory = {
    this.givenPlatform = givenPlatform
    this
  }

  def withNotificationKind(notificationsKind: DiagnosticNotificationsKind): LanguageServerFactory = {
    this.notificationsKind = notificationsKind
    this
  }

  def withDirectoryResolver(dr: ClientDirectoryResolver): LanguageServerFactory = {
    this.directoryResolver = Some(dr)
    this
  }

  def withAmfPlugins(plugin: Seq[AMFShapePayloadValidationPlugin]): LanguageServerFactory = {
    plugins = plugin
    this
  }

  def build(): LanguageServer = {
    val resourceLoaders     = if (rl.isEmpty) EditorConfiguration.platform.loaders() else rl
    val editorConfiguration = new EditorConfiguration(resourceLoaders, Seq.empty, plugins, logger)
    val factory             = new WorkspaceManagerFactoryBuilder(clientNotifier, logger, editorConfiguration)

    directoryResolver.foreach(cdr => factory.withDirectoryResolver(DirectoryResolverAdapter.convert(cdr)))
    factory.withNotificationKind(notificationsKind) // move to initialization param
    val dm                    = factory.buildDiagnosticManagers()
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
