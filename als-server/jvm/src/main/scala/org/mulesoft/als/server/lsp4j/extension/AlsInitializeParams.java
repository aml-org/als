package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.InitializeParams;

public class AlsInitializeParams extends InitializeParams {

    private AlsClientCapabilities alsClientCapabilities;
    private AlsConfiguration configuration;
    private ProjectConfigurationStyle projectConfigurationStyle;

    @Override
    public AlsClientCapabilities getCapabilities() {
        return alsClientCapabilities;
    }

    public void setCapabilities(AlsClientCapabilities capabilities) {
        alsClientCapabilities = capabilities;
    }

    public ClientCapabilities getClientCapabilities(){
        return super.getCapabilities();
    }

    @Override
    public void setCapabilities(ClientCapabilities capabilities) {
        AlsClientCapabilities acp = new AlsClientCapabilities();
        acp.setTextDocument(capabilities.getTextDocument());
        acp.setWorkspace(capabilities.getWorkspace());
        acp.setExperimental(capabilities.getExperimental());
        alsClientCapabilities = acp;
    }

    /**
     * hack to clean lsp4j native des serialized capabilities.
     */
    public void cleanInheritedCapabilities() {
        super.setCapabilities(null);
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
