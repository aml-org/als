package org.mulesoft.als.server.lsp4j.extension;

public class ConversionClientCapabilities {
    private Boolean supported;

    public ConversionClientCapabilities(Boolean supported) {
        this.supported = supported;
    }

    public Boolean getSupported() {
        return supported;
    }
}
