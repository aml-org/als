package org.mulesoft.lsp.server

import org.mulesoft.lsp.configuration.{InitializeParams, InitializeResult}
import org.mulesoft.lsp.feature.{RequestHandler, RequestType}
import org.mulesoft.lsp.textsync.TextDocumentSyncConsumer
import org.mulesoft.lsp.workspace.WorkspaceService

import scala.concurrent.Future

trait LanguageServer {
  def initialize(params: InitializeParams): Future[InitializeResult]

  def initialized(): Unit

  def shutdown(): Unit

  def exit(): Unit

  def textDocumentSyncConsumer: TextDocumentSyncConsumer

  def workspaceService: WorkspaceService

  def resolveHandler[P, R](requestType: RequestType[P, R]): Option[RequestHandler[P, R]]

}
