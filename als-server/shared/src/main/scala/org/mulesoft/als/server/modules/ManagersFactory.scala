package org.mulesoft.als.server.modules

import amf.core.remote.Platform
import org.mulesoft.als.common.{DirectoryResolver, PlatformDirectoryResolver}
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.actions.{DocumentLinksManager, GoToDefinitionManager}
import org.mulesoft.als.server.modules.completion.SuggestionsManager
import org.mulesoft.als.server.modules.diagnostic.DiagnosticManager
import org.mulesoft.als.server.modules.structure.StructureManager
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.modules.workspace.WorkspaceManager
import org.mulesoft.als.server.textsync.{TextDocumentContainer, TextDocumentManager}

case class ManagersFactory(clientNotifier: ClientNotifier,
                           platform: Platform,
                           logger: Logger,
                           dr: Option[DirectoryResolver] = None,
                           withDiagnostics: Boolean = true) {

  private val directoryResolver          = dr.getOrElse(new PlatformDirectoryResolver(platform))
  val telemetryManager: TelemetryManager = new TelemetryManager(clientNotifier, logger)
  // todo initialize amf
//  val astManager                         = new AstManager(editorEnvironment.environment, telemetryManager, platform, logger)

  lazy val diagnosticManager = new DiagnosticManager(telemetryManager, clientNotifier, logger)

  private val projectDependencies = if (withDiagnostics) List(diagnosticManager) else Nil
  private val container           = TextDocumentContainer(platform)

  private val workspaceManager = new WorkspaceManager(container, projectDependencies)
  lazy val documentManager     = new TextDocumentManager(container, List(workspaceManager), logger)

  lazy val completionManager =
    new SuggestionsManager(workspaceManager, telemetryManager, directoryResolver, platform, logger)

  lazy val structureManager = new StructureManager(workspaceManager, telemetryManager, logger)

  lazy val definitionManager =
    new GoToDefinitionManager(workspaceManager, telemetryManager, logger, platform)
  lazy val documentLinksManager =
    new DocumentLinksManager(workspaceManager, telemetryManager, logger, platform)
}
