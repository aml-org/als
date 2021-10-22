package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.InitializeParams;

public class AlsInitializeParams extends InitializeParams {

    private AlsClientCapabilities capabilities;
    private AlsConfiguration configuration;
    private ProjectConfigurationStyle projectConfigurationStyle;

    @Override
    public AlsClientCapabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(AlsClientCapabilities capabilities) {
        this.capabilities = capabilities;
    }

    @Override
    public void setCapabilities(ClientCapabilities capabilities) {
        AlsClientCapabilities acp = new AlsClientCapabilities();
        acp.setTextDocument(capabilities.getTextDocument());
        acp.setWorkspace(capabilities.getWorkspace());
        acp.setExperimental(capabilities.getExperimental());
        this.capabilities = acp;
    }
    
    public AlsConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(AlsConfiguration configuration) {
        this.configuration = configuration;
    }

    public ProjectConfigurationStyle getProjectConfigurationStyle() {
        return projectConfigurationStyle;
    }

    public void setProjectConfigurationStyle(ProjectConfigurationStyle projectConfigurationStyle) {
        this.projectConfigurationStyle = projectConfigurationStyle;
    }
}
