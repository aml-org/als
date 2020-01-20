package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.xtext.xbase.lib.Pure;

public class AlsClientCapabilities {

    private SerializationClientCapabilities serialization;

    @Pure
    public SerializationClientCapabilities getSerialization() {
        return this.serialization;
    }

    public void setSerialization(SerializationClientCapabilities serialization){
        this.serialization = serialization;
    }
}
