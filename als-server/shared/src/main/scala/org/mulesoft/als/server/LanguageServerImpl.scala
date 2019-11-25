package org.mulesoft.als.server

import org.mulesoft.lsp.configuration.{InitializeParams, InitializeResult}
import org.mulesoft.lsp.feature.{RequestHandler, RequestType}
import org.mulesoft.lsp.server.LanguageServer
import org.mulesoft.lsp.textsync.TextDocumentSyncConsumer
import org.mulesoft.lsp.workspace.WorkspaceService

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class LanguageServerImpl(val textDocumentSyncConsumer: TextDocumentSyncConsumer,
                         val workspaceService: WorkspaceService,
                         private val languageServerInitializer: LanguageServerInitializer,
                         private val requestHandlerMap: RequestMap)
    extends LanguageServer {

  override def initialize(params: InitializeParams): Future[InitializeResult] =
    languageServerInitializer.initialize(params).map { p =>
      params.rootUri.orElse(params.rootPath).foreach(root => workspaceService.initializeWS(root))
      p
    }

  override def initialized(): Unit = {}

  override def shutdown(): Unit = {}

  override def exit(): Unit = {}

  override def resolveHandler[P, R](requestType: RequestType[P, R]): Option[RequestHandler[P, R]] =
    requestHandlerMap(requestType)

}
