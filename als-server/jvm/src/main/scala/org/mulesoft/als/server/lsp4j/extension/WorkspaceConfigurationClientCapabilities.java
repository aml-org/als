package org.mulesoft.als.server.lsp4j.extension;


import org.eclipse.xtext.xbase.lib.Pure;

public class WorkspaceConfigurationClientCapabilities {
    private boolean get = true;

    @Pure
    public boolean canGet() {
        return get;
    }

    public void setGet(boolean get) {
        this.get = get;
    }
}
