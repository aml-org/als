package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.InitializedParams;
import org.eclipse.lsp4j.jsonrpc.services.JsonDelegate;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.concurrent.CompletableFuture;

public interface ExtendedLanguageServer {

    @JsonRequest
    CompletableFuture<AlsInitializeResult> initialize(AlsInitializeParams params);

    @JsonNotification
    void initialized(InitializedParams params);

    @JsonNotification
    void updateConfiguration(UpdateConfigurationParams params);

    @JsonRequest
    CompletableFuture<Object> shutdown();

    @JsonNotification
    void exit();

    @JsonDelegate
    TextDocumentService getTextDocumentService();

    @JsonDelegate
    WorkspaceService getWorkspaceService();
}
