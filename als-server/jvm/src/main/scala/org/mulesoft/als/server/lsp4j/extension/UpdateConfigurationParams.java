package org.mulesoft.als.server.lsp4j.extension;

public class UpdateConfigurationParams {

    private UpdateFormatOptionsParams updateFormatOptionsParams;

    public UpdateConfigurationParams(UpdateFormatOptionsParams updateFormatOptionsParams) {
        this.updateFormatOptionsParams = updateFormatOptionsParams;
    }

    public UpdateFormatOptionsParams getUpdateFormatOptionsParams() {
        return updateFormatOptionsParams;
    }
}
