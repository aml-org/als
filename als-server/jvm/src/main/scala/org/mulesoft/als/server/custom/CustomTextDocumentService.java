package org.mulesoft.als.server.custom;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.mulesoft.als.server.lsp4j.extension.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CustomTextDocumentService extends TextDocumentService{

    @JsonRequest
    default CompletableFuture<SerializedDocument> conversion(ConversionParams params) {
        throw new UnsupportedOperationException();
    }

    @JsonRequest
    default CompletableFuture<List<PublishDiagnosticsParams>> cleanDiagnosticTree(CleanDiagnosticTreeParams params) {
        throw new UnsupportedOperationException();
    }

    @JsonRequest
    default CompletableFuture<List<Location>> fileUsage(TextDocumentIdentifier params) {
        throw new UnsupportedOperationException();
    }

    @JsonRequest
    default CompletableFuture<SerializedDocument> serialization(SerializationParams params) {
        throw new UnsupportedOperationException();
    }

    @JsonRequest
    default CompletableFuture<RenameFileActionResult> renameFile(RenameFileActionParams params) {
        throw new UnsupportedOperationException();
    }
}
