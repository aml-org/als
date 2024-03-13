package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.TextDocumentIdentifier;

public class SerializationParams {
    private TextDocumentIdentifier documentIdentifier;
    private boolean clean;
    private boolean sourcemaps;

    public SerializationParams(TextDocumentIdentifier documentIdentifier, boolean clean, boolean sourcemaps) {
        this.documentIdentifier = documentIdentifier;
        this.clean = clean;
        this.sourcemaps = sourcemaps;
    }

    public SerializationParams(TextDocumentIdentifier documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
        this.clean = false;
        this.sourcemaps = false;
    }

    public TextDocumentIdentifier getDocumentIdentifier() {
        return documentIdentifier;
    }

    public void setDocumentIdentifier(TextDocumentIdentifier documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
    }

    public boolean getSourcemaps() {
        return sourcemaps;
    }

    public void setSourcemaps(boolean sourcemaps) {
        this.sourcemaps = sourcemaps;
    }

    public boolean getClean() {
        return clean;
    }

    public void setClean(boolean clean) {
        this.clean = clean;
    }
}
