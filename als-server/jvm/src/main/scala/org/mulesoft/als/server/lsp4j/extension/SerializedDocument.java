package org.mulesoft.als.server.lsp4j.extension;

public class SerializedDocument {
    private String uri;
    private String content;

    public SerializedDocument(String uri,String content) {
        this.uri = uri;
        this.content = content;
    }

    public String getUri() { return uri; }

    public String getContent() { return content; }
}
