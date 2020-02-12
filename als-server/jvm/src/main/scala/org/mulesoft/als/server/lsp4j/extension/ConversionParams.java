package org.mulesoft.als.server.lsp4j.extension;


public class ConversionParams {

    private String uri;
    private String target;
    private String syntax;

    public ConversionParams(String uri,String target, String syntax) {
        this.uri = uri;
        this.target = target;
        this.syntax = syntax;
    }

    public String getUri() {
        return uri;
    }

    public String getTarget() {
        return target;
    }

    public String getSyntax() {
        return syntax;
    }
}
