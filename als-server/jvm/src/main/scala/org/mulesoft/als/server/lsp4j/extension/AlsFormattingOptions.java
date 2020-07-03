package org.mulesoft.als.server.lsp4j.extension;

public class AlsFormattingOptions {
    private int tabSize;
    private boolean preferSpaces;

    public AlsFormattingOptions(int tabSize, boolean preferSpaces) {
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
