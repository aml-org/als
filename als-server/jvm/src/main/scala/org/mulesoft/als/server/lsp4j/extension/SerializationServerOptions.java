package org.mulesoft.als.server.lsp4j.extension;

public class SerializationServerOptions {

    private Boolean supportsSerialization;

    public SerializationServerOptions(Boolean supportsSerialization) {
        this.supportsSerialization = supportsSerialization;
    }

    public Boolean getSupportsSerialization() {
        return supportsSerialization;
    }
}
