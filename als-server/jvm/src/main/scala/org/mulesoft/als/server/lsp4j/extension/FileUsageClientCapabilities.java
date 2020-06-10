package org.mulesoft.als.server.lsp4j.extension;

public class FileUsageClientCapabilities {
    private Boolean enabled;

    public FileUsageClientCapabilities(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getEnabledFileUsage() {
        return enabled;
    }
}
