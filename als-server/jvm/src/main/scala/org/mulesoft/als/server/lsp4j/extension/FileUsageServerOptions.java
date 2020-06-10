package org.mulesoft.als.server.lsp4j.extension;

public class FileUsageServerOptions {

    private Boolean supported;

    public FileUsageServerOptions(Boolean supported) {
        this.supported = supported;
    }

    public Boolean getSupported() {
        return supported;
    }
}
