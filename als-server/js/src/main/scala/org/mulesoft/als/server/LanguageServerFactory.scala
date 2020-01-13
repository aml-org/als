package org.mulesoft.als.server

import amf.client.resource.ClientResourceLoader
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.{Logger, PrintLnLogger}
import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.modules.diagnostic.DiagnosticNotificationsKind
import org.mulesoft.als.suggestions.client.js.JsSuggestions.{emptyDirectoryResolver, internalResourceLoader}
import org.mulesoft.als.suggestions.client.js.{ClientDirectoryResolver, DirectoryResolverAdapter}
import org.mulesoft.lsp.server.{DefaultServerSystemConf, LanguageServer, LanguageServerSystemConf}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

case class JsServerSystemConf(clientLoaders: js.Array[ClientResourceLoader] = js.Array(),
                              clientDirResolver: ClientDirectoryResolver = emptyDirectoryResolver)
  extends LanguageServerSystemConf with PlatformSecrets {

  override def environment = Environment(clientLoaders.map(internalResourceLoader).toSeq)

  override def directoryResolver: DirectoryResolver = DirectoryResolverAdapter.convert(clientDirResolver)
}

@JSExportAll
@JSExportTopLevel("LanguageServerFactory")
object LanguageServerFactory {

  def fromLoaders(clientNotifier: ClientNotifier,
                  clientLoaders: js.Array[ClientResourceLoader] = js.Array(),
                  clientDirResolver: ClientDirectoryResolver = emptyDirectoryResolver,
                  logger: Logger = PrintLnLogger,
                  withDiagnostics: Boolean = true,
                  notificationKind: Option[DiagnosticNotificationsKind] = None): LanguageServer = {
    fromSystemConfig(clientNotifier, JsServerSystemConf(clientLoaders, clientDirResolver), logger, withDiagnostics, notificationKind)
  }


  def fromSystemConfig(clientNotifier: ClientNotifier,
                       languageServerSystemConf: LanguageServerSystemConf = DefaultServerSystemConf,
                       logger: Logger = PrintLnLogger,
                       withDiagnostics: Boolean = true,
                       notificationKind: Option[DiagnosticNotificationsKind] = None): LanguageServer = {

    val builders =
      ManagersFactory(clientNotifier,
        logger,
        withDiagnostics = withDiagnostics,
        configuration = languageServerSystemConf,
        notificationKind = notificationKind)

    new LanguageServerBuilder(builders.documentManager, builders.workspaceManager, languageServerSystemConf)
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
