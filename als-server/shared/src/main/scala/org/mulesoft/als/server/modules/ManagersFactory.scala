package org.mulesoft.als.server.modules

import org.mulesoft.als.common.{DirectoryResolver, PlatformDirectoryResolver}
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.actions.{DocumentLinksManager, FindReferenceManager, GoToDefinitionManager}
import org.mulesoft.als.server.modules.completion.SuggestionsManager
import org.mulesoft.als.server.modules.diagnostic.DiagnosticManager
import org.mulesoft.als.server.modules.structure.StructureManager
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.textsync.{TextDocumentContainer, TextDocumentManager}
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.server.{DefaultServerSystemConf, LanguageServerSystemConf}

case class ManagersFactory(clientNotifier: ClientNotifier,
                           logger: Logger,
                           dr: Option[DirectoryResolver] = None,
                           configuration: LanguageServerSystemConf = DefaultServerSystemConf,
                           withDiagnostics: Boolean = true) {

  private val directoryResolver          = dr.getOrElse(new PlatformDirectoryResolver(configuration.platform))
  val telemetryManager: TelemetryManager = new TelemetryManager(clientNotifier, logger)
  // todo initialize amf
  //  val astManager                         = new AstManager(editorEnvironment.environment, telemetryManager, platform, logger)

  lazy val diagnosticManager = new DiagnosticManager(telemetryManager, clientNotifier, logger)

  private val projectDependencies = if (withDiagnostics) List(diagnosticManager) else Nil
  val container                   = TextDocumentContainer(configuration)

  val workspaceManager     = new WorkspaceManager(container, telemetryManager, projectDependencies, logger, configuration)
  lazy val documentManager = new TextDocumentManager(container, List(workspaceManager), logger)

  lazy val completionManager =
    new SuggestionsManager(container, workspaceManager, telemetryManager, directoryResolver, configuration, logger)

  lazy val structureManager = new StructureManager(workspaceManager, telemetryManager, logger)

  lazy val definitionManager =
    new GoToDefinitionManager(workspaceManager, telemetryManager, logger, configuration)
  lazy val referenceManager =
    new FindReferenceManager(workspaceManager, telemetryManager, logger)
  lazy val documentLinksManager =
    new DocumentLinksManager(workspaceManager, telemetryManager, logger, configuration)
}
