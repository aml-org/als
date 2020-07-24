package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.xtext.xbase.lib.Pure;

public class AlsClientCapabilities extends ClientCapabilities {

    private SerializationClientCapabilities serialization;

    private CleanDiagnosticTreeClientCapabilities cleanDiagnosticTree;

    private FileUsageClientCapabilities fileUsage;

    private ConversionClientCapabilities conversion;

    private RenameFileActionClientCapabilities renameFileAction;

    @Pure
    public SerializationClientCapabilities getSerialization() {
        return this.serialization;
    }

    @Pure
    public FileUsageClientCapabilities getFileUsage() {
        return this.fileUsage;
    }

    @Pure
    public CleanDiagnosticTreeClientCapabilities getCleanDiagnosticTree() {
        return this.cleanDiagnosticTree;
    }

    @Pure
    public RenameFileActionClientCapabilities getRenameFileAction() {
        return renameFileAction;
    }

    public void setSerialization(SerializationClientCapabilities serialization){
        this.serialization = serialization;
    }

    public void setFileUsage(FileUsageClientCapabilities fileUsage){
        this.fileUsage = fileUsage;
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
