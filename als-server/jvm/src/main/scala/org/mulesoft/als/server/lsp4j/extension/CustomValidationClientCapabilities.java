package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.xtext.xbase.lib.Pure;

public class CustomValidationClientCapabilities {


    private boolean enabled;

    @Pure
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
