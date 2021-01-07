package org.mulesoft.als.server.lsp4j.extension;

import java.util.Map;

public class AlsConfiguration {

    private Boolean disableTemplates;

    private Map<String, AlsFormattingOptions> formattingOptions;

    public AlsConfiguration(Map<String, AlsFormattingOptions> formattingOptions, Boolean disableTemplates) {
        this.formattingOptions = formattingOptions;
        this.disableTemplates = disableTemplates;
    }

    public AlsConfiguration() {
    }

    public Map<String, AlsFormattingOptions> getFormattingOptions() {
        return formattingOptions;
    }

    public void setFormattingOptions(Map<String, AlsFormattingOptions> formattingOptions) {
        this.formattingOptions = formattingOptions;
    }

    public Boolean getDisableTemplates() {
        return disableTemplates ? disableTemplates : false;
    }

    public void setDisableTemplates(Boolean disableTemplates) {
        this.disableTemplates = disableTemplates;
    }
}
