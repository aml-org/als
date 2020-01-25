package org.mulesoft.lsp.server

import org.mulesoft.lsp.configuration.{
  AlsClientCapabilities,
  AlsServerCapabilities,
  AlsInitializeParams,
  AlsInitializeResult
}
import org.mulesoft.lsp.feature.{RequestHandler, RequestType}
import org.mulesoft.lsp.textsync.TextDocumentSyncConsumer
import org.mulesoft.lsp.workspace.WorkspaceService

import scala.concurrent.Future

trait LanguageServer {
  def initialize(params: AlsInitializeParams): Future[AlsInitializeResult]

  def initialized(): Unit

  def shutdown(): Unit

  def exit(): Unit

  def textDocumentSyncConsumer: TextDocumentSyncConsumer

  def workspaceService: WorkspaceService

  def resolveHandler[P, R](requestType: RequestType[P, R]): Option[RequestHandler[P, R]]

  def configuration: LanguageServerSystemConf
}
