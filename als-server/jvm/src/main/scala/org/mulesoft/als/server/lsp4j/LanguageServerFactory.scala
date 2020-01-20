package org.mulesoft.als.server.lsp4j

import amf.core.unsafe.PlatformSecrets
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.DiagnosticNotificationsKind
import org.mulesoft.als.server.{JvmSerializationProps, LanguageServerBuilder}
import org.mulesoft.amfmanager.CustomDialects
import org.mulesoft.lsp.server.{DefaultServerSystemConf, LanguageServer, LanguageServerSystemConf}

object LanguageServerFactory extends PlatformSecrets {

  def alsLanguageServer(clientNotifier: ClientNotifier,
                        serialization: JvmSerializationProps,
                        logger: Logger,
                        dialects: Seq[CustomDialects] = Seq(),
                        systemConfiguration: LanguageServerSystemConf = DefaultServerSystemConf,
                        withDiagnostics: Boolean = true,
                        notificationKind: Option[DiagnosticNotificationsKind] = None): LanguageServer = {

    // todo: uri to editor environment
    val factory = new WorkspaceManagerFactoryBuilder(clientNotifier, logger).withConfiguration(systemConfiguration)
    notificationKind.foreach(factory.withNotificationKind)
    val dm       = factory.diagnosticManager()
    val sm       = factory.serializationManager(serialization)
    val builders = factory.buildWorkspaceManagerFactory()

    val languageBuilder =
      new LanguageServerBuilder(builders.documentManager, builders.workspaceManager, systemConfiguration)
        .addInitializable(builders.workspaceManager)
        .addInitializableModule(dm)
        .addInitializableModule(sm)
        .addRequestModule(builders.completionManager)
        .addRequestModule(builders.structureManager)
        .addRequestModule(builders.definitionManager)
        .addRequestModule(builders.referenceManager)
        .addRequestModule(builders.documentLinksManager)
        .addInitializable(builders.telemetryManager)
    languageBuilder.build()
  }
}
