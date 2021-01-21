package org.mulesoft.als.server.lsp4j.extension;

import java.util.Map;

public class AlsConfiguration {

    private String templateType;

    private Map<String, AlsFormattingOptions> formattingOptions;

    public AlsConfiguration(Map<String, AlsFormattingOptions> formattingOptions, String templateType) {
        this.formattingOptions = formattingOptions;
        this.templateType = templateType;
    }

    public AlsConfiguration() {
    }

    public Map<String, AlsFormattingOptions> getFormattingOptions() {
        return formattingOptions;
    }

    public void setFormattingOptions(Map<String, AlsFormattingOptions> formattingOptions) {
        this.formattingOptions = formattingOptions;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }
}
