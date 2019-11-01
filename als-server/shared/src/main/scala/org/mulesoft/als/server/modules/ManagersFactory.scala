package org.mulesoft.als.server.modules

import amf.core.remote.Platform
import org.mulesoft.als.common.{DirectoryResolver, PlatformDirectoryResolver}
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.actions.{DocumentLinksManager, GoToDefinitionManager}
import org.mulesoft.als.server.modules.ast.{AstManager, EditorEnvironment, ProjectManager}
import org.mulesoft.als.server.modules.completion.SuggestionsManager
import org.mulesoft.als.server.modules.diagnostic.DiagnosticManager
import org.mulesoft.als.server.modules.structure.StructureManager
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.textsync.TextDocumentManager

case class ManagersFactory(clientNotifier: ClientNotifier,
                           platform: Platform,
                           logger: Logger,
                           dr: Option[DirectoryResolver] = None) {

  private val directoryResolver          = dr.getOrElse(new PlatformDirectoryResolver(platform))
  private val editorEnvironment          = EditorEnvironment(platform)
  val telemetryManager: TelemetryManager = new TelemetryManager(clientNotifier, logger)
  val astManager                         = new AstManager(editorEnvironment.environment, telemetryManager, platform, logger)
  lazy val diagnosticManager             = new DiagnosticManager(editorEnvironment, telemetryManager, clientNotifier, logger)

  private val projectManager = new ProjectManager(editorEnvironment.unitsRepositories,
                                                  astManager,
                                                  scala.collection.immutable.List(diagnosticManager))

  lazy val documentManager = new TextDocumentManager(editorEnvironment.memoryFiles, List(projectManager), logger)

  lazy val completionManager =
    new SuggestionsManager(editorEnvironment, telemetryManager, directoryResolver, platform, logger)

  lazy val structureManager = new StructureManager(editorEnvironment.unitsRepositories, telemetryManager, logger)

  lazy val definitionManager =
    new GoToDefinitionManager(editorEnvironment.unitsRepositories, telemetryManager, logger, platform)
  lazy val documentLinksManager =
    new DocumentLinksManager(editorEnvironment.unitsRepositories, telemetryManager, logger, platform)
}
