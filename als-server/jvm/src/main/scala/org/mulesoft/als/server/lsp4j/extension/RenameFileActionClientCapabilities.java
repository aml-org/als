package org.mulesoft.als.server.lsp4j.extension;

public class RenameFileActionClientCapabilities {
    private Boolean enabled;

    public RenameFileActionClientCapabilities(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getEnabled() {
        return enabled;
    }
}
