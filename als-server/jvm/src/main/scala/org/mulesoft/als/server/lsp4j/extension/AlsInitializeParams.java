package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.InitializeParams;

public class AlsInitializeParams extends InitializeParams {

    private AlsClientCapabilities alsClientCapabilities;
    private AlsConfiguration configuration;
    private Boolean hotReload;
    private Boolean newCachingLogic;

    @Override
    public AlsClientCapabilities getCapabilities() {
        return alsClientCapabilities;
    }

    public void setCapabilities(AlsClientCapabilities capabilities) {
        this.alsClientCapabilities = capabilities;
    }

    @Override
    public void setCapabilities(ClientCapabilities capabilities) {
        AlsClientCapabilities acp = new AlsClientCapabilities();
        acp.setTextDocument(capabilities.getTextDocument());
        acp.setWorkspace(capabilities.getWorkspace());
        acp.setExperimental(capabilities.getExperimental());
        this.alsClientCapabilities = acp;
    }

    public AlsConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(AlsConfiguration configuration) {
        this.configuration = configuration;
    }

    public Boolean getHotReload() {
        return hotReload;
    }

    public void setHotReload(Boolean hotReload) {
        this.hotReload = hotReload;
    }

    public Boolean getNewCachingLogic() {
        return newCachingLogic;
    }

    public void setNewCachingLogic(Boolean p) {
        this.newCachingLogic = p;
    }

}
