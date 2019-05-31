package org.mulesoft.als.server.lsp4j

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture

import org.eclipse.lsp4j
import org.eclipse.lsp4j.jsonrpc.services.JsonDelegate
import org.eclipse.lsp4j.services.{LanguageServer, WorkspaceService}
import org.mulesoft.als.server.custom.CustomTextDocumentService
import org.mulesoft.als.server.lsp4j.Lsp4JConversions._
import org.mulesoft.als.server.lsp4j.LspConversions._
import org.mulesoft.lsp.server

import scala.concurrent.ExecutionContext.Implicits.global

class LanguageServerImpl(private val inner: server.LanguageServer) extends LanguageServer {

  private val textDocumentService = new TextDocumentServiceImpl(inner)

  override def initialize(params: lsp4j.InitializeParams): CompletableFuture[lsp4j.InitializeResult] =
    javaFuture(inner.initialize(params), lsp4JInitializeResult)

  override def initialized(params: lsp4j.InitializedParams): Unit = inner.initialized()

  override def shutdown(): CompletableFuture[AnyRef] = {
    inner.shutdown()
    completedFuture("ok")
  }

  override def exit(): Unit = inner.exit()

  @JsonDelegate
  override def getTextDocumentService: CustomTextDocumentService = textDocumentService

  override def getWorkspaceService: WorkspaceService = new WorkspaceServiceImpl()

}
