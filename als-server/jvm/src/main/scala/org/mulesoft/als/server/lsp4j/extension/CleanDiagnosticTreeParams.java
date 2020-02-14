package org.mulesoft.als.server.lsp4j.extension;


import org.eclipse.lsp4j.TextDocumentIdentifier;

public class CleanDiagnosticTreeParams {
    private TextDocumentIdentifier textDocument;

    public CleanDiagnosticTreeParams(TextDocumentIdentifier document) {
        this.textDocument = document;
    }

    public TextDocumentIdentifier getTextDocument() {
        return textDocument;
    }
}
