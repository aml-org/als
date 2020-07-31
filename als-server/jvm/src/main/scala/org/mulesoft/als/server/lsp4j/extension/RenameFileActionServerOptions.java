package org.mulesoft.als.server.lsp4j.extension;

public class RenameFileActionServerOptions {
    private Boolean supported;

    public RenameFileActionServerOptions(Boolean supported) {
        this.supported = supported;
    }

    public Boolean getSupported() {
        return supported;
    }
}
