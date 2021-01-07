package org.mulesoft.als.server.lsp4j.extension;

import java.util.Map;

public class UpdateConfigurationParams {

    private Map<String, AlsFormattingOptions> formattingOptions;

    private Boolean disableTemplates;

    private Map<String, Object> genericOptions;

    public UpdateConfigurationParams(Map<String, AlsFormattingOptions> updateFormatOptionsParams, Boolean disableTemplates) {
        this.formattingOptions = updateFormatOptionsParams;
    }

    public Map<String, AlsFormattingOptions> getUpdateFormatOptionsParams() {
        return formattingOptions;
    }

    public Map<String, Object> getGenericOptions() {
        return genericOptions;
    }

    public Boolean getDisableTemplates() {
        return disableTemplates;
    }

    public void setDisableTemplates(Boolean disableTemplates) {
        this.disableTemplates = disableTemplates;
    }
}
