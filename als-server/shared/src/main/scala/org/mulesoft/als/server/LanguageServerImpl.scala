package org.mulesoft.als.server

import org.mulesoft.lsp.configuration.{
  AlsClientCapabilities,
  AlsServerCapabilities,
  InitializeParams,
  InitializeResult
}
import org.mulesoft.lsp.feature.{RequestHandler, RequestType}
import org.mulesoft.lsp.server.{DefaultServerSystemConf, LanguageServer, LanguageServerSystemConf}
import org.mulesoft.lsp.textsync.TextDocumentSyncConsumer
import org.mulesoft.lsp.workspace.WorkspaceService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LanguageServerImpl(val textDocumentSyncConsumer: TextDocumentSyncConsumer,
                         val workspaceService: WorkspaceService,
                         private val languageServerInitializer: LanguageServerInitializer,
                         private val requestHandlerMap: RequestMap,
                         private val systemConfiguration: LanguageServerSystemConf = DefaultServerSystemConf)
    extends LanguageServer {

  override def initialize(params: InitializeParams): Future[InitializeResult] =
    languageServerInitializer.initialize(params).flatMap { p =>
      params.rootUri.orElse(params.rootPath) match {
        case Some(root) => workspaceService.initializeWS(root).map(_ => p)
        case _          => Future.successful(p)
      }
    }

  override def initialized(): Unit = {}

  override def shutdown(): Unit = {}

  override def exit(): Unit = {}

  override def resolveHandler[P, R](requestType: RequestType[P, R]): Option[RequestHandler[P, R]] =
    requestHandlerMap(requestType)

  override def configuration: LanguageServerSystemConf = systemConfiguration

  override def notifyAlsClientCapabilities(clientCapabilities: AlsClientCapabilities): AlsServerCapabilities =
    languageServerInitializer.applyAlsCapabilities(clientCapabilities)
}
