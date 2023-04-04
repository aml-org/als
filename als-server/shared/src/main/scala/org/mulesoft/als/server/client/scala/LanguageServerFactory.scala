package org.mulesoft.als.server.client.scala

import amf.aml.client.scala.model.document.DialectInstance
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import amf.custom.validation.client.ProfileValidatorWebBuilder
import amf.custom.validation.client.scala.{BaseProfileValidatorBuilder, CustomValidator, ProfileValidatorExecutor}
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.logger.{Logger, PrintLnLogger}
import org.mulesoft.als.server.client.platform.ClientNotifier
import org.mulesoft.als.server.modules.ast.WorkspaceContentListener
import org.mulesoft.als.server.modules.diagnostic.{DiagnosticNotificationsKind, PARSING_BEFORE}
import org.mulesoft.als.server.modules.{WorkspaceManagerFactory, WorkspaceManagerFactoryBuilder}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.textsync.TextDocumentSyncBuilder
import org.mulesoft.als.server.workspace.ProjectConfigurationProvider
import org.mulesoft.als.server.{EmptySerializationProps, SerializationProps}
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration

import scala.concurrent.ExecutionContext.Implicits.global

class LanguageServerFactory(
    clientNotifier: ClientNotifier,
    profileValidatorBuilder: BaseProfileValidatorBuilder = ProfileValidatorWebBuilder
) {
  protected var serialization: SerializationProps[_]                        = new EmptySerializationProps
  protected var logger: Logger                                              = PrintLnLogger
  protected var notificationsKind: DiagnosticNotificationsKind              = PARSING_BEFORE
  protected var directoryResolver: Option[DirectoryResolver]                = None
  protected var rl: Seq[ResourceLoader]                                     = EditorConfiguration.platform.loaders()
  protected var plugins: Seq[AMFShapePayloadValidationPlugin]               = Seq.empty
  protected var amfCustomValidatorBuilder: BaseProfileValidatorBuilder      = profileValidatorBuilder
  protected var configurationProvider: Option[ProjectConfigurationProvider] = None
  protected var textDocumentSyncBuilder: Option[TextDocumentSyncBuilder]    = None
  protected var workspaceContentListeners: Seq[WorkspaceContentListener[_]] = Seq.empty

  def withSerializationProps(serializationProps: SerializationProps[_]): this.type = {
    serialization = serializationProps
    this
  }

  def withResourceLoaders(rl: Seq[ResourceLoader]): this.type = {
    this.rl = rl
    this
  }

  def withAdditionalResourceLoaders(r: Seq[ResourceLoader]): this.type = {
    this.rl = r ++ rl
    this
  }

  def withAdditionalResourceLoader(r: ResourceLoader): this.type = {
    this.rl = r +: rl
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

  def withAmfCustomValidator(customValidator: CustomValidator): this.type = {
    val builder = new BaseProfileValidatorBuilder {
      override def validator(profile: DialectInstance): ProfileValidatorExecutor =
        ProfileValidatorExecutor(customValidator, profile = profile)
    }

    amfCustomValidatorBuilder = builder
    this
  }

  def withProjectConfigurationProvider(configProvider: Option[ProjectConfigurationProvider]): this.type = {
    configurationProvider = configProvider
    this
  }

  def withTextDocumentSyncBuilder(givenTextDocumentSyncBuilder: TextDocumentSyncBuilder): this.type = {
    textDocumentSyncBuilder = Some(givenTextDocumentSyncBuilder)
    this
  }

  // todo @eascona: too specific, this should be "withWorkspaceContentListener" or something of the sorts, and add a listener to the existing list
  def withWorkspaceConfigListener(workspaceContentListener: WorkspaceContentListener[_]): this.type = {
    workspaceContentListeners = workspaceContentListeners :+ workspaceContentListener
    this
  }

  def build(): LanguageServer = {
    val resourceLoaders     = if (rl.isEmpty) EditorConfiguration.platform.loaders() else rl
    val editorConfiguration = new EditorConfiguration(resourceLoaders, Seq.empty, plugins, logger)
    val factory =
      new WorkspaceManagerFactoryBuilder(
        clientNotifier,
        logger,
        editorConfiguration,
        configurationProvider,
        textDocumentSyncBuilder
      )

    directoryResolver.foreach(cdr => factory.withDirectoryResolver(cdr))
    factory.withNotificationKind(notificationsKind) // move to initialization param
    val dm                    = factory.buildDiagnosticManagers(Some(amfCustomValidatorBuilder))
    val sm                    = factory.serializationManager(serialization)
    val filesInProjectManager = factory.filesInProjectManager(serialization.alsClientNotifier)
    val profileNotification   = factory.profileNotificationConfigurationListener(serialization)
    val dialectNotification   = factory.dialectNotificationListener(serialization)
    workspaceContentListeners.foreach(factory.addWorkspaceContentListener)

    val builders = factory.buildWorkspaceManagerFactory()

    val languageBuilder =
      languageServerWithBasicFeatures(builders)
        .addInitializableModule(sm)
        .addInitializableModule(filesInProjectManager)
        .addInitializableModule(profileNotification)
        .addInitializableModule(dialectNotification)

    dm.foreach(m => languageBuilder.addInitializableModule(m))
    builders.serializationManager.foreach(languageBuilder.addRequestModule)
    languageBuilder.build()
  }

  protected def languageServerWithBasicFeatures(builders: WorkspaceManagerFactory): LanguageServerBuilder =
    new LanguageServerBuilder(
      builders.documentManager,
      builders.workspaceManager,
      builders.configurationManager,
      builders.resolutionTaskManager,
      logger
    )
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
}
