package org.mulesoft.als.server.lsp4j.extension;

public class SerializedDocument {
    private String uri;
    private String model;

    public SerializedDocument(String uri,String content) {
        this.uri = uri;
        this.model = content;
    }

    public String getUri() { return uri; }

    public String getModel() { return model; }
}
