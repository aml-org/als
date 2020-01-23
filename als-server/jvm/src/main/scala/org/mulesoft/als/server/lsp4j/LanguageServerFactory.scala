package org.mulesoft.als.server.lsp4j

import amf.client.plugins.AMFPlugin
import amf.core.unsafe.PlatformSecrets
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.DiagnosticNotificationsKind
import org.mulesoft.als.server.{JvmSerializationProps, LanguageServerBuilder}
import org.mulesoft.lsp.server.{AmfConfiguration, DefaultServerSystemConf, LanguageServer, LanguageServerSystemConf}

import scala.collection.JavaConverters._
object LanguageServerFactory extends PlatformSecrets {

  def alsLanguageServer(clientNotifier: ClientNotifier,
                        serialization: JvmSerializationProps,
                        logger: Logger,
                        systemConfiguration: LanguageServerSystemConf = DefaultServerSystemConf,
                        notificationKind: Option[DiagnosticNotificationsKind] = None,
                        amfPlugins: java.util.List[AMFPlugin] = new java.util.ArrayList()): LanguageServer = {

    // todo: uri to editor environment
    val factory = new WorkspaceManagerFactoryBuilder(clientNotifier, logger)
      .withAmfConfiguration(new AmfConfiguration(amfPlugins.asScala, systemConfiguration))
    notificationKind.foreach(factory.withNotificationKind)
    val dm       = factory.diagnosticManager()
    val sm       = factory.serializationManager(serialization)
    val fip      = factory.filesInProjectManager(serialization.alsClientNotifier)
    val builders = factory.buildWorkspaceManagerFactory()

    val languageBuilder =
      new LanguageServerBuilder(builders.documentManager, builders.workspaceManager)
        .addInitializable(builders.workspaceManager)
        .addInitializableModule(dm)
        .addInitializableModule(sm)
        .addInitializableModule(fip)
        .addRequestModule(builders.cleanDiagnosticManager)
        .addRequestModule(builders.completionManager)
        .addRequestModule(builders.structureManager)
        .addRequestModule(builders.definitionManager)
        .addRequestModule(builders.referenceManager)
        .addRequestModule(builders.documentLinksManager)
        .addInitializable(builders.telemetryManager)
    languageBuilder.build()
  }
}
