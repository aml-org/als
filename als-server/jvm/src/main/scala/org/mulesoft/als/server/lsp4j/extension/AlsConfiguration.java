package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.FormattingOptions;

import java.util.Map;

public class AlsConfiguration {
    private Map<String, AlsFormattingOptions> formattingOptions;

    public Map<String, AlsFormattingOptions> getFormattingOptions() {
        return formattingOptions;
    }

}
