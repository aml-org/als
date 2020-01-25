package org.mulesoft.als.server

import amf.client.resource.ClientResourceLoader
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.server.client.{AlsClientNotifier, ClientNotifier}
import org.mulesoft.als.server.logger.{Logger, PrintLnLogger}
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.DiagnosticNotificationsKind
import org.mulesoft.als.suggestions.client.js.JsSuggestions.{emptyDirectoryResolver, internalResourceLoader}
import org.mulesoft.als.suggestions.client.js.{ClientDirectoryResolver, DirectoryResolverAdapter}
import org.mulesoft.lsp.server.{DefaultServerSystemConf, LanguageServer, LanguageServerSystemConf}
import org.yaml.builder.{DocBuilder, JsOutputBuilder}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

case class JsServerSystemConf(clientLoaders: js.Array[ClientResourceLoader] = js.Array(),
                              clientDirResolver: ClientDirectoryResolver = emptyDirectoryResolver)
    extends LanguageServerSystemConf
    with PlatformSecrets {

  override def environment = Environment(clientLoaders.map(internalResourceLoader).toSeq)

  override def directoryResolver: DirectoryResolver = DirectoryResolverAdapter.convert(clientDirResolver)
}

@JSExportAll
@JSExportTopLevel("LanguageServerFactory")
object LanguageServerFactory {

  def fromLoaders(clientNotifier: ClientNotifier with AlsClientNotifier[js.Any],
                  clientLoaders: js.Array[ClientResourceLoader] = js.Array(),
                  clientDirResolver: ClientDirectoryResolver = emptyDirectoryResolver,
                  logger: Logger = PrintLnLogger,
                  withDiagnostics: Boolean = true,
                  notificationKind: Option[DiagnosticNotificationsKind] = None): LanguageServer = {
    fromSystemConfig(clientNotifier,
                     JsServerSystemConf(clientLoaders, clientDirResolver),
                     logger,
                     withDiagnostics,
                     notificationKind)
  }

  def fromSystemConfig(clientNotifier: ClientNotifier with AlsClientNotifier[js.Any],
                       languageServerSystemConf: LanguageServerSystemConf = DefaultServerSystemConf,
                       logger: Logger = PrintLnLogger,
                       withDiagnostics: Boolean = true,
                       notificationKind: Option[DiagnosticNotificationsKind] = None): LanguageServer = {

    val builders = new WorkspaceManagerFactoryBuilder(clientNotifier, logger)
      .withConfiguration(languageServerSystemConf)
    val dm = builders.diagnosticManager()
    val sm = builders.serializationManager(JsSerializationProps(clientNotifier))

    notificationKind.foreach(builders.withNotificationKind)
    val factory = builders.buildWorkspaceManagerFactory()
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, languageServerSystemConf)
      .addInitializableModule(dm)
      .addInitializableModule(sm)
      .addInitializable(factory.workspaceManager)
      .addRequestModule(factory.completionManager)
      .addRequestModule(factory.structureManager)
      .addRequestModule(factory.definitionManager)
      .addRequestModule(factory.referenceManager)
      .addRequestModule(factory.documentLinksManager)
      .addInitializable(factory.telemetryManager)
      .build()
  }
}

case class JsSerializationProps(override val alsClientNotifier: AlsClientNotifier[js.Any])
    extends SerializationProps[js.Any](alsClientNotifier) {
  override def newDocBuilder(): DocBuilder[js.Any] = JsOutputBuilder()
}
