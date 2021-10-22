package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.Set;

public class WorkspaceConfigurationParams {
    private String mainUri;
    private String folder;
    private Set<Either<String, DependencyConfiguration>> dependencies;


    public WorkspaceConfigurationParams(String mainUri, String folder, Set<Either<String, DependencyConfiguration>> dependencies) {
        this.mainUri = mainUri;
        this.folder = folder;
        this.dependencies = dependencies;
    }

    public WorkspaceConfigurationParams() {}

    public String getMainUri() {
        return mainUri;
    }

    public void setMainUri(String mainUri) {
        this.mainUri = mainUri;
    }

    public Set<Either<String, DependencyConfiguration>> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<Either<String, DependencyConfiguration>> dependencies) {
        this.dependencies = dependencies;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
