package org.mulesoft.als.server.lsp4j.extension;

public class CleanDiagnosticTreeClientCapabilities {
    private Boolean enabledClean;

    public CleanDiagnosticTreeClientCapabilities(Boolean enabledClean) {
        this.enabledClean = enabledClean;
    }

    public Boolean getEnabledCleanDiagnostic() {
        return enabledClean;
    }
}
