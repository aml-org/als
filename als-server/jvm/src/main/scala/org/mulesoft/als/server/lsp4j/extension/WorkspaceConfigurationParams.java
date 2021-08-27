package org.mulesoft.als.server.lsp4j.extension;

import java.util.Set;

public class WorkspaceConfigurationParams {
    private String mainUri;
    private Set<String> dependencies;
    private Set<String> customValidationProfiles;
    private Set<String> semanticExtensions;


    public WorkspaceConfigurationParams(String mainUri, Set<String> dependencies, Set<String> customValidationProfiles, Set<String> semanticExtensions) {
        this.mainUri = mainUri;
        this.dependencies = dependencies;
        this.customValidationProfiles = customValidationProfiles;
        this.semanticExtensions = semanticExtensions;
    }

    public WorkspaceConfigurationParams() {}

    public String getMainUri() {
        return mainUri;
    }

    public void setMainUri(String mainUri) {
        this.mainUri = mainUri;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<String> dependencies) {
        this.dependencies = dependencies;
    }

    public Set<String> getCustomValidationProfiles() {
        return customValidationProfiles;
    }

    public void setCustomValidationProfiles(Set<String> customValidationProfiles) {
        this.customValidationProfiles = customValidationProfiles;
    }
    public Set<String> getSemanticExtensions() {
        return semanticExtensions;
    }

    public void setSemanticExtensions(Set<String> semanticExtensions) {
        this.semanticExtensions = semanticExtensions;
    }
}
