package org.mulesoft.als.server.lsp4j.extension;

public class UpdateFormatOptionsParams {

    private int tabSize;
    private boolean preferSpaces;

    public UpdateFormatOptionsParams(int tabSize, boolean preferSpaces) {
        this.tabSize = tabSize;
        this.preferSpaces = preferSpaces;
    }

    public int getTabSize() {
        return tabSize;
    }

    public boolean preferSpaces() {
        return preferSpaces;
    }
}
