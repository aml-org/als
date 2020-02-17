package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.TextDocumentIdentifier;

public class SerializationParams {
    private TextDocumentIdentifier documentIdentifier;

    public SerializationParams(TextDocumentIdentifier documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
    }

    public TextDocumentIdentifier getDocumentIdentifier() {
        return documentIdentifier;
    }

    public void setDocumentIdentifier(TextDocumentIdentifier documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
    }
}
