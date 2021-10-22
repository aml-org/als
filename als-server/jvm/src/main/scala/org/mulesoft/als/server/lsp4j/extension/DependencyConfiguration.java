package org.mulesoft.als.server.lsp4j.extension;


public class DependencyConfiguration {
    private String file;
    private String scope;


    public DependencyConfiguration(String file, String scope) {
        this.file = file;
        this.scope = scope;
    }

    public DependencyConfiguration() {}

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getScope() {
        return scope;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
}
