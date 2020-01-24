package org.mulesoft.als.server.modules

import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import org.mulesoft.als.common.{DirectoryResolver, PlatformDirectoryResolver}
import org.mulesoft.als.server.SerializationProps
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.actions.{DocumentLinksManager, FindReferenceManager, GoToDefinitionManager}
import org.mulesoft.als.server.modules.ast.BaseUnitListener
import org.mulesoft.als.server.modules.completion.SuggestionsManager
import org.mulesoft.als.server.modules.diagnostic.{
  ALL_TOGETHER,
  CleanDiagnosticTreeManager,
  DiagnosticManager,
  DiagnosticNotificationsKind
}
import org.mulesoft.als.server.modules.serialization.SerializationManager
import org.mulesoft.als.server.modules.structure.StructureManager
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.textsync.{TextDocumentContainer, TextDocumentManager}
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.InitializableModule
import org.mulesoft.lsp.server.AmfInstance

import scala.collection.mutable.ListBuffer

class WorkspaceManagerFactoryBuilder(clientNotifier: ClientNotifier, logger: Logger) extends PlatformSecrets {

  private var amfConfig: AmfInstance                        = AmfInstance.default
  private var notificationKind: DiagnosticNotificationsKind = ALL_TOGETHER
  private var givenPlatform                                 = platform
  private var environment                                   = Environment()
  private var directoryResolver: DirectoryResolver          = new PlatformDirectoryResolver(platform)
  def withAmfConfiguration(amfConfig: AmfInstance): WorkspaceManagerFactoryBuilder = {
    this.amfConfig = amfConfig
    this
  }

  def withNotificationKind(nk: DiagnosticNotificationsKind): WorkspaceManagerFactoryBuilder = {
    notificationKind = nk
    this
  }

  def withPlatform(p: Platform): WorkspaceManagerFactoryBuilder = {
    givenPlatform = p
    this
  }

  def withEnvironment(environment: Environment): WorkspaceManagerFactoryBuilder = {
    this.environment = environment
    this
  }

  def withDirectoryResolver(directoryResolver: DirectoryResolver): WorkspaceManagerFactoryBuilder = {
    this.directoryResolver = directoryResolver
    this
  }
  private val projectDependencies: ListBuffer[InitializableModule[_, _] with BaseUnitListener] = ListBuffer()

  val telemetryManager: TelemetryManager = new TelemetryManager(clientNotifier, logger)

  def serializationManager[S](sp: SerializationProps[S]): SerializationManager[S] = {
    val s = new SerializationManager(amfConfig, sp)
    projectDependencies += s
    s
  }

  def diagnosticManager(): DiagnosticManager = {
    val dm = new DiagnosticManager(telemetryManager, clientNotifier, logger, notificationKind)
    projectDependencies += dm
    dm
  }

  def buildWorkspaceManagerFactory(): WorkspaceManagerFactory =
    WorkspaceManagerFactory(projectDependencies.toList,
                            telemetryManager,
                            environment,
                            platform,
                            directoryResolver,
                            logger,
                            amfConfig)
}

case class WorkspaceManagerFactory(projectDependencies: List[BaseUnitListener],
                                   telemetryManager: TelemetryManager,
                                   environment: Environment,
                                   platform: Platform,
                                   directoryResolver: DirectoryResolver,
                                   logger: Logger,
                                   amfConfiguration: AmfInstance) {
  val container: TextDocumentContainer = TextDocumentContainer(environment, platform, amfConfiguration)

  val cleanDiagnosticManager = new CleanDiagnosticTreeManager(container, logger)
  val workspaceManager       = new WorkspaceManager(container, telemetryManager, projectDependencies, logger)
  lazy val documentManager   = new TextDocumentManager(container, List(workspaceManager), logger)

  lazy val completionManager =
    new SuggestionsManager(container, workspaceManager, telemetryManager, directoryResolver, logger)

  lazy val structureManager = new StructureManager(workspaceManager, telemetryManager, logger)

  lazy val definitionManager =
    new GoToDefinitionManager(workspaceManager, platform, telemetryManager, logger)
  lazy val referenceManager =
    new FindReferenceManager(workspaceManager, telemetryManager, logger)
  lazy val documentLinksManager =
    new DocumentLinksManager(workspaceManager, telemetryManager, platform, logger)
}
