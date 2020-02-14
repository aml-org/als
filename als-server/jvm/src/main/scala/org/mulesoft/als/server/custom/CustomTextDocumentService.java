package org.mulesoft.als.server.custom;

import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.mulesoft.als.server.lsp4j.extension.CleanDiagnosticTreeParams;
import org.mulesoft.als.server.lsp4j.extension.ConversionParams;
import org.mulesoft.als.server.lsp4j.extension.SerializationParams;
import org.mulesoft.als.server.lsp4j.extension.SerializedDocument;

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
    default CompletableFuture<SerializedDocument> serialization(SerializationParams params) {
        throw new UnsupportedOperationException();
    }
}
