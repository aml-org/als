package org.mulesoft.als.server.lsp4j.extension;

public class WorkspaceConfigurationOptions {
    private boolean supported = true;

    public boolean isSupported() {
        return supported;
    }

    public void setSupported(boolean supported) {
        this.supported = supported;
    }
}
