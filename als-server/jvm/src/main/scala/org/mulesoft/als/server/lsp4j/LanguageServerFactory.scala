package org.mulesoft.als.server.lsp4j

import amf.core.unsafe.PlatformSecrets
import org.mulesoft.als.server.LanguageServerBuilder
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.amfmanager.CustomDialects
import org.mulesoft.lsp.server.LanguageServer

object LanguageServerFactory extends PlatformSecrets {

  def alsLanguageServer(clientNotifier: ClientNotifier,
                        logger: Logger,
                        dialects: Seq[CustomDialects] = Seq()): LanguageServer = {

// todo: uri toeditor enviroment
    val builders = ManagersFactory(clientNotifier, platform, logger)

    new LanguageServerBuilder(builders.documentManager)
      .addInitializable(builders.astManager)
      .addInitializable(builders.diagnosticManager)
      .addRequestModule(builders.completionManager)
      .addRequestModule(builders.structureManager)
      .addRequestModule(builders.definitionManager)
      .addRequestModule(builders.documentLinksManager)
      .addInitializable(builders.telemetryManager)
      .build()
  }
}
