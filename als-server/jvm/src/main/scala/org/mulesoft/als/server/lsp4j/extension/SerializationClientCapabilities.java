package org.mulesoft.als.server.lsp4j.extension;

public class SerializationClientCapabilities {
    private Boolean supportsSerialization;

    public SerializationClientCapabilities(Boolean supportsSerialization){
        this.supportsSerialization = supportsSerialization;
    }

    public Boolean getSupportsSerialization() {
        return supportsSerialization;
    }
}
