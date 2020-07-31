package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.RenameFile;
import org.eclipse.lsp4j.TextDocumentEdit;
import org.eclipse.lsp4j.WorkspaceEdit;

import java.util.List;

public class RenameFileActionResult {
    private WorkspaceEdit edits;

    public RenameFileActionResult(WorkspaceEdit edits) {
        this.edits = edits;
    }

    public WorkspaceEdit getEdits() {
        return edits;
    }

    public void setEdits(WorkspaceEdit edits) {
        this.edits = edits;
    }
}
