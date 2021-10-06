package org.mulesoft.als.server.lsp4j.extension;

public class CustomValidationOptions {
    private boolean enabled;

    public CustomValidationOptions(boolean enabled) {
        this.enabled = enabled;
    }

    public CustomValidationOptions() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
