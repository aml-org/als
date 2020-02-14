package org.mulesoft.als.server.lsp4j.extension;

public class CleanDiagnosticTreeServerOptions {

    private Boolean supported;

    public CleanDiagnosticTreeServerOptions(Boolean supported) {
        this.supported = supported;
    }

    public Boolean getSupported() {
        return supported;
    }
}
