package org.mulesoft.als.server.lsp4j

import amf.core.unsafe.PlatformSecrets
import org.mulesoft.als.server.LanguageServerBuilder
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.amfmanager.CustomDialects
import org.mulesoft.lsp.server.{DefaultServerSystemConf, LanguageServer, LanguageServerSystemConf}

object LanguageServerFactory extends PlatformSecrets {

  def alsLanguageServer(clientNotifier: ClientNotifier,
                        logger: Logger,
                        dialects: Seq[CustomDialects] = Seq(),
                        systemConfiguration: LanguageServerSystemConf = DefaultServerSystemConf,
                        withDiagnostics: Boolean = true): LanguageServer = {

    // todo: uri to editor environment
    val builders =
      ManagersFactory(clientNotifier, logger, withDiagnostics = withDiagnostics, configuration = systemConfiguration)

    new LanguageServerBuilder(builders.documentManager, builders.workspaceManager, systemConfiguration)
      .addInitializable(builders.diagnosticManager)
      .addInitializable(builders.workspaceManager)
      .addRequestModule(builders.completionManager)
      .addRequestModule(builders.structureManager)
      .addRequestModule(builders.definitionManager)
      .addRequestModule(builders.referenceManager)
      .addRequestModule(builders.documentLinksManager)
      .addInitializable(builders.telemetryManager)
      .build()
  }
}
