package org.mulesoft.als.server.lsp4j.extension;

public class GetWorkspaceConfigurationResult {
    private String workspace;
    private WorkspaceConfigurationParams configuration;

    public GetWorkspaceConfigurationResult(String workspace, WorkspaceConfigurationParams configuration) {
        this.workspace = workspace;
        this.configuration = configuration;
    }

    public GetWorkspaceConfigurationResult() { }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public WorkspaceConfigurationParams getConfiguration() {
        return configuration;
    }

    public void setConfiguration(WorkspaceConfigurationParams configuration) {
        this.configuration = configuration;
    }
}
