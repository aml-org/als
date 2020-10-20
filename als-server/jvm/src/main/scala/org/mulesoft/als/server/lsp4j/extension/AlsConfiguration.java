package org.mulesoft.als.server.lsp4j.extension;

import java.util.Map;

public class AlsConfiguration {
    private Map<String, AlsFormattingOptions> formattingOptions;

    public AlsConfiguration(Map<String, AlsFormattingOptions> formattingOptions) {
        this.formattingOptions = formattingOptions;
    }

    public AlsConfiguration() {
    }

    public Map<String, AlsFormattingOptions> getFormattingOptions() {
        return formattingOptions;
    }

    public void setFormattingOptions(Map<String, AlsFormattingOptions> formattingOptions) {
        this.formattingOptions = formattingOptions;
    }
}
