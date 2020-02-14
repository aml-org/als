package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.xtext.xbase.lib.Pure;

public class AlsClientCapabilities extends ClientCapabilities {

    private SerializationClientCapabilities serialization;

    private CleanDiagnosticTreeClientCapabilities cleanDiagnosticTree;

    private ConversionClientCapabilities conversion;

    @Pure
    public SerializationClientCapabilities getSerialization() {
        return this.serialization;
    }

    @Pure
    public CleanDiagnosticTreeClientCapabilities getCleanDiagnosticTree() {
        return this.cleanDiagnosticTree;
    }

    public void setSerialization(SerializationClientCapabilities serialization){
        this.serialization = serialization;
    }

    public void setCleanDiagnosticTree(CleanDiagnosticTreeClientCapabilities cleanDiagnosticTree){
        this.cleanDiagnosticTree = cleanDiagnosticTree;
    }

    public void setConversionClientCapabilities(ConversionClientCapabilities conversion){
        this.conversion = conversion;
    }

    @Pure
    public ConversionClientCapabilities getConversion() {
        return conversion;
    }
}
