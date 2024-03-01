package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.TextDocumentIdentifier;

public class SerializationParams {
    private TextDocumentIdentifier documentIdentifier;
    private Boolean clean;
    private Boolean sourcemaps;

    public SerializationParams(TextDocumentIdentifier documentIdentifier, Boolean clean, Boolean sourcemaps) {
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

    public Boolean getSourcemaps() {
        return sourcemaps;
    }

    public void setSourcemaps(Boolean sourcemaps) {
        this.sourcemaps = sourcemaps;
    }

    public Boolean getClean() {
        return clean;
    }

    public void setClean(Boolean clean) {
        this.clean = clean;
    }
}
