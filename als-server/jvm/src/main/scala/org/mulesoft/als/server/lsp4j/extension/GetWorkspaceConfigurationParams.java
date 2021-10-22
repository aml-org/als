package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.TextDocumentIdentifier;

public class GetWorkspaceConfigurationParams {
    private TextDocumentIdentifier textDocument;

    public TextDocumentIdentifier getTextDocument() {
        return textDocument;
    }

    public void setTextDocument(TextDocumentIdentifier textDocument) {
        this.textDocument = textDocument;
    }
}
