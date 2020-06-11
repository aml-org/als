package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.FormattingOptions;

public class AlsConfiguration {
    private FormattingOptions formattingOptions;

    public FormattingOptions getFormattingOptions() {
        return formattingOptions;
    }

    public void setFormattingOptions(FormattingOptions formattingOptions) {
        this.formattingOptions = formattingOptions;
    }
}
