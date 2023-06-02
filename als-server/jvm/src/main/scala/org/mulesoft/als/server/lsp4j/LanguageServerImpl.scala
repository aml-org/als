package org.mulesoft.als.server.lsp4j

import org.eclipse.lsp4j
import org.eclipse.lsp4j.jsonrpc.services.JsonDelegate
import org.mulesoft.als.server.ALSConverters.ClientList
import org.mulesoft.als.server.custom.CustomTextDocumentService
import org.mulesoft.als.server.lsp4j.AlsJConversions._
import org.mulesoft.als.server.lsp4j.LspConversions._
import org.mulesoft.als.server.lsp4j.extension.{
  AlsInitializeParams,
  AlsInitializeResult,
  ExtendedLanguageServer,
  UpdateConfigurationParams
}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.lsp.Lsp4JConversions._

import java.util
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import scala.concurrent.ExecutionContext.Implicits.global
class LanguageServerImpl(private val inner: LanguageServer) extends ExtendedLanguageServer {

  def workspaceFolders(): ClientList[String] = {
    val workspaces = new util.ArrayList[String]()
    inner.workspaceFolders().foreach(workspaces.add)
    workspaces
  }

  private val textDocumentService = new TextDocumentServiceImpl(inner)

  private val workspaceService = new WorkspaceServiceImpl(inner)

  override def initialize(params: AlsInitializeParams): CompletableFuture[AlsInitializeResult] =
    javaFuture(inner.initialize(params), alsInitializeResult)

  override def initialized(params: lsp4j.InitializedParams): Unit = inner.initialized()

  override def shutdown(): CompletableFuture[AnyRef] = {
    inner.shutdown()
    completedFuture("ok")
  }

  override def exit(): Unit = inner.exit()

  @JsonDelegate
  override def getTextDocumentService: CustomTextDocumentService = textDocumentService

  override def getWorkspaceService: WorkspaceServiceImpl = workspaceService

  override def updateConfiguration(params: UpdateConfigurationParams): Unit = inner.updateConfiguration(params)
}
