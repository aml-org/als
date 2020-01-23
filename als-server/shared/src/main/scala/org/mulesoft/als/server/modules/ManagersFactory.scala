package org.mulesoft.als.server.modules

import org.mulesoft.als.server.SerializationProps
import org.mulesoft.als.server.client.{AlsClientNotifier, ClientNotifier}
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
import org.mulesoft.als.server.modules.workspace.FilesInProjectManager
import org.mulesoft.als.server.textsync.{TextDocumentContainer, TextDocumentManager}
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.InitializableModule
import org.mulesoft.lsp.server.{AmfConfiguration, DefaultAmfConfiguration}

import scala.collection.mutable.ListBuffer

class WorkspaceManagerFactoryBuilder(clientNotifier: ClientNotifier, logger: Logger) {

  private var amfConfig: AmfConfiguration                   = DefaultAmfConfiguration
  private var notificationKind: DiagnosticNotificationsKind = ALL_TOGETHER

  def withAmfConfiguration(amfConfig: AmfConfiguration): WorkspaceManagerFactoryBuilder = {
    this.amfConfig = amfConfig
    this
  }

  def withNotificationKind(nk: DiagnosticNotificationsKind): WorkspaceManagerFactoryBuilder = {
    notificationKind = nk
    this
  }

  private val projectDependencies: ListBuffer[InitializableModule[_, _] with BaseUnitListener] = ListBuffer()

  val telemetryManager: TelemetryManager = new TelemetryManager(clientNotifier, logger)

  def serializationManager[S](sp: SerializationProps[S]): SerializationManager[S] = {
    val s = new SerializationManager(amfConfig, sp)
    projectDependencies += s
    s
  }

  def filesInProjectManager(alsClientNotifier: AlsClientNotifier[_]): FilesInProjectManager = {
    val fip = new FilesInProjectManager(alsClientNotifier)
    projectDependencies += fip
    fip
  }

  def diagnosticManager(): DiagnosticManager = {
    val dm = new DiagnosticManager(telemetryManager, clientNotifier, logger, notificationKind)
    projectDependencies += dm
    dm
  }

  def buildWorkspaceManagerFactory(): WorkspaceManagerFactory =
    WorkspaceManagerFactory(projectDependencies.toList, telemetryManager, logger, amfConfig)
}

case class WorkspaceManagerFactory(projectDependencies: List[BaseUnitListener],
                                   telemetryManager: TelemetryManager,
                                   logger: Logger,
                                   amfConfiguration: AmfConfiguration) {
  val container: TextDocumentContainer = TextDocumentContainer(amfConfiguration)

  val cleanDiagnosticManager = new CleanDiagnosticTreeManager(container, logger)
  val workspaceManager       = new WorkspaceManager(container, telemetryManager, projectDependencies, logger)
  lazy val documentManager   = new TextDocumentManager(container, List(workspaceManager), logger)

  lazy val completionManager =
    new SuggestionsManager(container, workspaceManager, telemetryManager, logger)

  lazy val structureManager = new StructureManager(workspaceManager, telemetryManager, logger)

  lazy val definitionManager =
    new GoToDefinitionManager(workspaceManager, telemetryManager, logger)
  lazy val referenceManager =
    new FindReferenceManager(workspaceManager, telemetryManager, logger)
  lazy val documentLinksManager =
    new DocumentLinksManager(workspaceManager, telemetryManager, logger)
}
