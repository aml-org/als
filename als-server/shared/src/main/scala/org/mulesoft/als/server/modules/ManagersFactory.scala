package org.mulesoft.als.server.modules

import amf.core.internal.unsafe.PlatformSecrets
import amf.custom.validation.client.scala.BaseProfileValidatorBuilder
import org.mulesoft.als.actions.codeactions.plugins.AllCodeActions
import org.mulesoft.als.common.{DirectoryResolver, PlatformDirectoryResolver}
import org.mulesoft.als.server.SerializationProps
import org.mulesoft.als.server.client.platform.{AlsClientNotifier, ClientNotifier}
import org.mulesoft.als.server.modules.actions._
import org.mulesoft.als.server.modules.actions.fileusage.FindFileUsageManager
import org.mulesoft.als.server.modules.actions.rename.RenameManager
import org.mulesoft.als.server.modules.ast.{AccessUnits, ResolvedUnitListener, WorkspaceContentListener}
import org.mulesoft.als.server.modules.completion.SuggestionsManager
import org.mulesoft.als.server.modules.configuration.{ConfigurationManager, WorkspaceConfigurationManager}
import org.mulesoft.als.server.modules.diagnostic._
import org.mulesoft.als.server.modules.diagnostic.custom.CustomValidationManager
import org.mulesoft.als.server.modules.project.{DialectChangeListener, ProfileChangeListener}
import org.mulesoft.als.server.modules.serialization.{ConversionManager, SerializationManager}
import org.mulesoft.als.server.modules.structure.StructureManager
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.modules.workspace.resolution.ResolutionTaskManager
import org.mulesoft.als.server.modules.workspace.{
  CompilableUnit,
  DefaultProjectConfigurationProvider,
  FilesInProjectManager
}
import org.mulesoft.als.server.protocol.textsync.AlsTextDocumentSyncConsumer
import org.mulesoft.als.server.textsync.{DefaultTextDocumentSyncBuilder, TextDocumentContainer, TextDocumentSyncBuilder}
import org.mulesoft.als.server.workspace.{ProjectConfigurationProvider, WorkspaceManager}
import org.mulesoft.amfintegration.AmfResolvedUnit
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration

import scala.collection.mutable.ListBuffer

class WorkspaceManagerFactoryBuilder(
    clientNotifier: ClientNotifier,
    val editorConfiguration: EditorConfiguration = EditorConfiguration(),
    projectConfigurationProvider: Option[ProjectConfigurationProvider] = None,
    textDocumentSyncBuilder: Option[TextDocumentSyncBuilder] = None
) extends PlatformSecrets {

  private var notificationKind: DiagnosticNotificationsKind = ALL_TOGETHER
  private var directoryResolver: DirectoryResolver =
    new PlatformDirectoryResolver(platform)
  private var customValidationManager: Option[CustomValidationManager] = None

  val configurationManager: ConfigurationManager = new ConfigurationManager()

  def withNotificationKind(nk: DiagnosticNotificationsKind): WorkspaceManagerFactoryBuilder = {
    notificationKind = nk
    this
  }

  def withDirectoryResolver(directoryResolver: DirectoryResolver): WorkspaceManagerFactoryBuilder = {
    this.directoryResolver = directoryResolver
    this
  }

  private val projectDependencies: ListBuffer[WorkspaceContentListener[_]] = ListBuffer()
  private val resolutionDependencies: ListBuffer[ResolvedUnitListener] =
    ListBuffer()

  val telemetryManager: TelemetryManager =
    new TelemetryManager(clientNotifier)

  def serializationManager[S](sp: SerializationProps[S]): SerializationManager[S] = {
    val s =
      new SerializationManager(telemetryManager, editorConfiguration, configurationManager.getConfiguration, sp)
    resolutionDependencies += s
    s
  }

  def buildDiagnosticManagers(
      customValidatorBuilder: Option[BaseProfileValidatorBuilder] = None
  ): Seq[BasicDiagnosticManager[_, _]] = {
    val gatherer = new ValidationGatherer(telemetryManager)
    val dm =
      new ParseDiagnosticManager(telemetryManager, clientNotifier, gatherer, notificationKind)
    val pdm = new ProjectDiagnosticManager(telemetryManager, clientNotifier, gatherer, notificationKind)
    val rdm = new ResolutionDiagnosticManager(telemetryManager, clientNotifier, gatherer)
    customValidationManager = customValidatorBuilder.map(validator =>
      new CustomValidationManager(telemetryManager, clientNotifier, gatherer, validator)
    )
    customValidationManager.foreach(resolutionDependencies += _)
    projectDependencies += pdm
    resolutionDependencies += rdm
    projectDependencies += dm
    Seq(pdm, dm, rdm) ++ customValidationManager
  }

  def filesInProjectManager(alsClientNotifier: AlsClientNotifier[_]): FilesInProjectManager = {
    val fip = new FilesInProjectManager(alsClientNotifier)
    projectDependencies += fip
    fip
  }

  def profileNotificationConfigurationListener[S](sp: SerializationProps[S]): ProfileChangeListener[S] = {
    val pnl =
      new ProfileChangeListener(sp, configurationManager.getConfiguration)
    projectDependencies += pnl
    pnl
  }

  def dialectNotificationListener[S](sp: SerializationProps[S]): DialectChangeListener[S] = {
    val dcl =
      new DialectChangeListener(sp, configurationManager.getConfiguration)
    projectDependencies += dcl
    dcl
  }

  def addWorkspaceContentListener(workspaceContentListener: WorkspaceContentListener[_]): Unit =
    projectDependencies += workspaceContentListener

  def buildWorkspaceManagerFactory(): WorkspaceManagerFactory =
    WorkspaceManagerFactory(
      projectDependencies.toList,
      resolutionDependencies.toList,
      telemetryManager,
      directoryResolver,
      configurationManager,
      editorConfiguration,
      customValidationManager,
      projectConfigurationProvider,
      textDocumentSyncBuilder
    )
}

case class WorkspaceManagerFactory(
    projectDependencies: List[WorkspaceContentListener[_]],
    resolutionDependencies: List[ResolvedUnitListener],
    telemetryManager: TelemetryManager,
    directoryResolver: DirectoryResolver,
    configurationManager: ConfigurationManager,
    editorConfiguration: EditorConfiguration,
    customValidationManager: Option[CustomValidationManager],
    projectConfigurationProvider: Option[ProjectConfigurationProvider],
    textDocumentSyncBuilder: Option[TextDocumentSyncBuilder]
) {
  val container: TextDocumentContainer =
    TextDocumentContainer()

  lazy val cleanDiagnosticManager = new CleanDiagnosticTreeManager(
    telemetryManager,
    container,
    customValidationManager,
    workspaceConfigurationManager
  )

  val resolutionTaskManager: ResolutionTaskManager = ResolutionTaskManager(
    telemetryManager,
    resolutionDependencies,
    resolutionDependencies.collect { case t: AccessUnits[AmfResolvedUnit] =>
      t // is this being used? is it correct to mix subscribers with this dependencies?
    }
  )

  private val dependencies: List[WorkspaceContentListener[_]] = projectDependencies :+ resolutionTaskManager

  val workspaceManager: WorkspaceManager =
    WorkspaceManager(
      container,
      telemetryManager,
      editorConfiguration,
      projectConfigurationProvider.getOrElse(
        new DefaultProjectConfigurationProvider(
          container,
          editorConfiguration
        )
      ),
      dependencies,
      dependencies.collect { case t: AccessUnits[CompilableUnit] =>
        t // is this being used? is it correct to mix subscribers with this dependencies?
      },
      configurationManager
    )

  lazy val documentManager: AlsTextDocumentSyncConsumer =
    textDocumentSyncBuilder
      .getOrElse(DefaultTextDocumentSyncBuilder)
      .build(container, List(workspaceManager))

  lazy val completionManager =
    new SuggestionsManager(
      container,
      workspaceManager,
      telemetryManager,
      directoryResolver,
      configurationManager
    )

  lazy val structureManager =
    new StructureManager(workspaceManager, telemetryManager)

  lazy val definitionManager =
    new GoToDefinitionManager(workspaceManager, telemetryManager)

  lazy val implementationManager =
    new GoToImplementationManager(workspaceManager, telemetryManager)

  lazy val typeDefinitionManager =
    new GoToTypeDefinitionManager(workspaceManager, telemetryManager)

  lazy val hoverManager = new HoverManager(workspaceManager, telemetryManager)

  lazy val referenceManager =
    new FindReferenceManager(workspaceManager, telemetryManager)

  lazy val fileUsageManager =
    new FindFileUsageManager(workspaceManager, telemetryManager)

  lazy val documentLinksManager =
    new DocumentLinksManager(workspaceManager, telemetryManager)

  lazy val renameManager =
    new RenameManager(
      workspaceManager,
      telemetryManager,
      configurationManager.getConfiguration,
      EditorConfiguration.platform
    )

  lazy val conversionManager =
    new ConversionManager(workspaceManager, telemetryManager)

  lazy val documentHighlightManager =
    new DocumentHighlightManager(workspaceManager, telemetryManager)

  lazy val foldingRangeManager =
    new FoldingRangeManager(workspaceManager, telemetryManager)

  lazy val selectionRangeManager =
    new SelectionRangeManager(workspaceManager, telemetryManager)

  lazy val renameFileActionManager: RenameFileActionManager =
    new RenameFileActionManager(
      workspaceManager,
      telemetryManager,
      configurationManager.getConfiguration,
      EditorConfiguration.platform
    )

  lazy val codeActionManager: CodeActionManager =
    new CodeActionManager(
      AllCodeActions.all,
      workspaceManager,
      configurationManager.getConfiguration,
      telemetryManager,
      directoryResolver
    )

  lazy val documentFormattingManager: DocumentFormattingManager =
    new DocumentFormattingManager(workspaceManager, telemetryManager)

  lazy val documentRangeFormattingManager: DocumentRangeFormattingManager =
    new DocumentRangeFormattingManager(workspaceManager, telemetryManager)

  lazy val serializationManager: Option[SerializationManager[_]] =
    resolutionDependencies.collectFirst({ case s: SerializationManager[_] =>
      s.withUnitAccessor(resolutionTaskManager) // is this redundant with the initialization of workspace manager?
      s
    })

  lazy val workspaceConfigurationManager: WorkspaceConfigurationManager =
    new WorkspaceConfigurationManager(workspaceManager, telemetryManager)

}
