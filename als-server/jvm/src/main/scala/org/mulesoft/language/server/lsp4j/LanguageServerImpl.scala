package org.mulesoft.language.server.lsp4j

import java.util.Collections
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture

import org.eclipse.lsp4j._
import org.eclipse.lsp4j.services.{LanguageServer, TextDocumentService, WorkspaceService}
import org.mulesoft.language.client.jvm.JAVAPlatformDependentPart
import org.mulesoft.language.server.core.Server
import org.mulesoft.language.server.core.connections.ServerConnection
import org.mulesoft.language.server.modules.astManager.{ASTManager, ASTManagerModule}
import org.mulesoft.language.server.modules.dialectManager.{DialectManager, DialectManagerModule}
import org.mulesoft.language.server.modules.hlastManager.HlAstManager
import org.mulesoft.language.server.modules.outline.StructureManager
import org.mulesoft.language.server.modules.suggestions.SuggestionsManager
import org.mulesoft.language.server.modules.validationManager.ValidationManager

import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext.Implicits.global

class LanguageServerImpl(val connection: ServerConnection,
                         val textDocumentService: TextDocumentService,
                         val workspaceService: WorkspaceService
                        ) extends LanguageServer {
  override def initialize(params: InitializeParams): CompletableFuture[InitializeResult] = {
    val server = new Server(connection, JAVAPlatformDependentPart)
    val serverCapabilities = new ServerCapabilities()

    serverCapabilities.setTextDocumentSync(TextDocumentSyncKind.Full)

    server.registerModule(new ASTManager())
    server.registerModule(new DialectManager())
    server.registerModule(new HlAstManager())

    server.registerModule(new ValidationManager())

    server.registerModule(new SuggestionsManager())
    serverCapabilities.setCompletionProvider(new CompletionOptions(true, Collections.emptyList()))

//    server.registerModule(new StructureManager())
//    serverCapabilities.setDocumentSymbolProvider(true)

//    server.registerModule(new FindReferencesModule())
//    serverCapabilities.setReferencesProvider(true)

//    server.registerModule(new FindDeclarationModule())
//    serverCapabilities.setDefinitionProvider(true)

//    server.registerModule(new RenameModule())
//    serverCapabilities.setRenameProvider(true)

    server.enableModule(ASTManagerModule.moduleId)
        .flatMap(_ => server.enableModule(DialectManagerModule.moduleId))
        .flatMap(_ => server.enableModule(HlAstManager.moduleId))
        .flatMap(_ => server.enableModule(ValidationManager.moduleId))
        .flatMap(_ => server.enableModule(SuggestionsManager.moduleId))
        .flatMap(_ => server.enableModule(StructureManager.moduleId))
//    server.enableModule(FindReferencesModule.moduleId)
//    server.enableModule(FindDeclarationModule.moduleId)
//    server.enableModule(RenameModule.moduleId)
        .map(_ => new InitializeResult(serverCapabilities))
        .toJava.toCompletableFuture
  }

  override def initialized(params: InitializedParams): Unit = super.initialized(params)

  override def shutdown(): CompletableFuture[AnyRef] = {
    completedFuture("ok")
  }

  override def exit(): Unit = {}

  override def getTextDocumentService: TextDocumentService = textDocumentService

  override def getWorkspaceService: WorkspaceService = workspaceService


}
