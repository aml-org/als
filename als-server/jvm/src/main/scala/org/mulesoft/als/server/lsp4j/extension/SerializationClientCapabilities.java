package org.mulesoft.als.server.lsp4j.extension;

public class SerializationClientCapabilities {
    private Boolean acceptsNotification;

    public SerializationClientCapabilities(Boolean acceptsNotification){
        this.acceptsNotification = acceptsNotification;
    }

    public Boolean getAcceptsNotification() {
        return acceptsNotification;
    }
}
