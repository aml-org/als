package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.TextDocumentIdentifier;

public class RenameFileActionParams {
    private TextDocumentIdentifier oldDocument;
    private TextDocumentIdentifier newDocument;

    public RenameFileActionParams(TextDocumentIdentifier oldDocument, TextDocumentIdentifier newDocument) {
        this.oldDocument = oldDocument;
        this.newDocument = newDocument;
    }

    public TextDocumentIdentifier getOldDocument() {
        return oldDocument;
    }

    public TextDocumentIdentifier getNewDocument() {
        return newDocument;
    }
}
