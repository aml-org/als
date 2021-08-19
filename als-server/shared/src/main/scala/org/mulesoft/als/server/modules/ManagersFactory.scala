package org.mulesoft.als.server.modules

import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.actions.codeactions.plugins.AllCodeActions
import org.mulesoft.als.common.{DirectoryResolver, PlatformDirectoryResolver}
import org.mulesoft.als.server.SerializationProps
import org.mulesoft.als.server.client.{AlsClientNotifier, ClientNotifier}
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.actions._
import org.mulesoft.als.server.modules.actions.fileusage.FindFileUsageManager
import org.mulesoft.als.server.modules.actions.rename.RenameManager
import org.mulesoft.als.server.modules.ast.{AccessUnits, BaseUnitListener, ResolvedUnitListener}
import org.mulesoft.als.server.modules.completion.SuggestionsManager
import org.mulesoft.als.server.modules.configuration.{ConfigurationManager, WorkspaceConfigurationManager}
import org.mulesoft.als.server.modules.diagnostic._
import org.mulesoft.als.server.modules.diagnostic.custom.{AMFOpaValidator, CustomValidationManager}
import org.mulesoft.als.server.modules.serialization.{ConversionManager, SerializationManager}
import org.mulesoft.als.server.modules.structure.StructureManager
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.modules.workspace.resolution.ResolutionTaskManager
import org.mulesoft.als.server.modules.workspace.{CompilableUnit, FilesInProjectManager}
import org.mulesoft.als.server.textsync.{TextDocumentContainer, TextDocumentManager}
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfintegration.AmfResolvedUnit
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper

import scala.collection.mutable.ListBuffer

class WorkspaceManagerFactoryBuilder(clientNotifier: ClientNotifier, logger: Logger, rs: Seq[ResourceLoader] = Nil)
    extends PlatformSecrets {

  private var amfConfiguration: AmfConfigurationWrapper     = AmfConfigurationWrapper(rs)
  private var notificationKind: DiagnosticNotificationsKind = ALL_TOGETHER
  private var directoryResolver: DirectoryResolver =
    new PlatformDirectoryResolver(platform)

  def getConfig: AmfConfigurationWrapper = amfConfiguration

  def withAmfConfiguration(amfConfig: AmfConfigurationWrapper): WorkspaceManagerFactoryBuilder = {
    this.amfConfiguration = amfConfig
    this
  }

  val configurationManager: ConfigurationManager = new ConfigurationManager()

  def withNotificationKind(nk: DiagnosticNotificationsKind): WorkspaceManagerFactoryBuilder = {
    notificationKind = nk
    this
  }

  def withDirectoryResolver(directoryResolver: DirectoryResolver): WorkspaceManagerFactoryBuilder = {
    this.directoryResolver = directoryResolver
    this
  }
  private val projectDependencies: ListBuffer[BaseUnitListener] = ListBuffer()
  private val resolutionDependencies: ListBuffer[ResolvedUnitListener] =
    ListBuffer()

  val telemetryManager: TelemetryManager =
    new TelemetryManager(clientNotifier, logger)

  def serializationManager[S](sp: SerializationProps[S]): SerializationManager[S] = {
    val s =
      new SerializationManager(telemetryManager, amfConfiguration, configurationManager.getConfiguration, sp, logger)
    resolutionDependencies += s
    s
  }

  def buildDiagnosticManagers(customValidator: Option[AMFOpaValidator] = None): Seq[DiagnosticManager] = {
    val gatherer = new ValidationGatherer(telemetryManager)
    val dm =
      new ParseDiagnosticManager(telemetryManager,
                                 clientNotifier,
                                 logger,
                                 amfConfiguration,
                                 gatherer,
                                 notificationKind)
    val rdm = new ResolutionDiagnosticManager(telemetryManager, clientNotifier, logger, gatherer, amfConfiguration)
    val cvm = customValidator.map(
      new CustomValidationManager(telemetryManager, clientNotifier, logger, gatherer, _, amfConfiguration))
    cvm.foreach(resolutionDependencies += _)
    resolutionDependencies += rdm
    projectDependencies += dm
    Seq(Some(dm), Some(rdm), cvm).flatten
  }

  def filesInProjectManager(alsClientNotifier: AlsClientNotifier[_]): FilesInProjectManager = {
    val fip = new FilesInProjectManager(alsClientNotifier)
    projectDependencies += fip
    fip
  }

  def buildWorkspaceManagerFactory(): WorkspaceManagerFactory =
    WorkspaceManagerFactory(projectDependencies.toList,
                            resolutionDependencies.toList,
                            telemetryManager,
                            directoryResolver,
                            logger,
                            amfConfiguration,
                            configurationManager)
}

case class WorkspaceManagerFactory(projectDependencies: List[BaseUnitListener],
                                   resolutionDependencies: List[ResolvedUnitListener],
                                   telemetryManager: TelemetryManager,
                                   directoryResolver: DirectoryResolver,
                                   logger: Logger,
                                   amfConfiguration: AmfConfigurationWrapper,
                                   configurationManager: ConfigurationManager) {
  val container: TextDocumentContainer =
    TextDocumentContainer(amfConfiguration)

  val cleanDiagnosticManager = new CleanDiagnosticTreeManager(telemetryManager, container, logger)

  val resolutionTaskManager: ResolutionTaskManager = ResolutionTaskManager(
    telemetryManager,
    logger,
    container,
    resolutionDependencies,
    resolutionDependencies.collect {
      case t: AccessUnits[AmfResolvedUnit] =>
        t // is this being used? is it correct to mix subscribers with this dependencies?
    }
  )

  private val dependencies: List[BaseUnitListener] = projectDependencies :+ resolutionTaskManager

  val workspaceManager: WorkspaceManager =
    WorkspaceManager(
      container,
      telemetryManager,
      dependencies,
      dependencies.collect {
        case t: AccessUnits[CompilableUnit] =>
          t // is this being used? is it correct to mix subscribers with this dependencies?
      },
      logger
    )

  lazy val documentManager =
    new TextDocumentManager(container, List(workspaceManager), logger)

  lazy val completionManager =
    new SuggestionsManager(container,
                           workspaceManager,
                           telemetryManager,
                           directoryResolver,
                           logger,
                           configurationManager)

  lazy val structureManager =
    new StructureManager(workspaceManager, telemetryManager, logger)

  lazy val definitionManager =
    new GoToDefinitionManager(workspaceManager, telemetryManager, logger)

  lazy val implementationManager =
    new GoToImplementationManager(workspaceManager, telemetryManager, logger)

  lazy val typeDefinitionManager =
    new GoToTypeDefinitionManager(workspaceManager, telemetryManager, logger)

  lazy val hoverManager = new HoverManager(workspaceManager, telemetryManager)

  lazy val referenceManager =
    new FindReferenceManager(workspaceManager, telemetryManager, logger)

  lazy val fileUsageManager =
    new FindFileUsageManager(workspaceManager, telemetryManager, logger)

  lazy val documentLinksManager =
    new DocumentLinksManager(workspaceManager, telemetryManager, logger)

  lazy val renameManager =
    new RenameManager(workspaceManager,
                      telemetryManager,
                      logger,
                      configurationManager.getConfiguration,
                      amfConfiguration.platform)

  lazy val conversionManager =
    new ConversionManager(workspaceManager, telemetryManager, amfConfiguration, logger)

  lazy val documentHighlightManager =
    new DocumentHighlightManager(workspaceManager, telemetryManager, logger)

  lazy val foldingRangeManager =
    new FoldingRangeManager(workspaceManager, telemetryManager, logger)

  lazy val selectionRangeManager =
    new SelectionRangeManager(workspaceManager, telemetryManager, logger)

  lazy val renameFileActionManager: RenameFileActionManager =
    new RenameFileActionManager(workspaceManager,
                                telemetryManager,
                                logger,
                                configurationManager.getConfiguration,
                                amfConfiguration.platform)

  lazy val codeActionManager: CodeActionManager =
    new CodeActionManager(AllCodeActions.all,
                          workspaceManager,
                          configurationManager.getConfiguration,
                          telemetryManager,
                          amfConfiguration,
                          logger,
                          directoryResolver)

  lazy val documentFormattingManager: DocumentFormattingManager =
    new DocumentFormattingManager(workspaceManager, telemetryManager, logger)

  lazy val documentRangeFormattingManager: DocumentRangeFormattingManager =
    new DocumentRangeFormattingManager(workspaceManager, telemetryManager, logger)

  lazy val serializationManager: Option[SerializationManager[_]] =
    resolutionDependencies.collectFirst({
      case s: SerializationManager[_] =>
        s.withUnitAccessor(resolutionTaskManager) // is this redundant with the initialization of workspace manager?
        s
    })

  lazy val workspaceConfigurationManager: WorkspaceConfigurationManager =
    new WorkspaceConfigurationManager(workspaceManager, telemetryManager, logger)

}
