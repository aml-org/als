package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.ServerCapabilities;

public class AlsInitializeResult {

    private AlsServerCapabilities capabilities;

    public AlsServerCapabilities getServerCapabilities() {
        return capabilities;
    }

    public void setServerCapabilities(AlsServerCapabilities serverCapabilities) {
        this.capabilities = serverCapabilities;
    }

    public ServerCapabilities getCapabilities() {
        return this.capabilities;
    }

    public void setCapabilities(ServerCapabilities capabilities) {
        AlsServerCapabilities asp = new AlsServerCapabilities();
        asp.setCallHierarchyProvider(capabilities.getCallHierarchyProvider());
        asp.setCodeActionProvider(capabilities.getCodeActionProvider());
        asp.setCodeLensProvider(capabilities.getCodeLensProvider());
        asp.setColorProvider(capabilities.getColorProvider());
        asp.setCompletionProvider(capabilities.getCompletionProvider());
        asp.setDefinitionProvider(capabilities.getDefinitionProvider());
        asp.setDocumentFormattingProvider(capabilities.getDocumentFormattingProvider());
        asp.setDocumentHighlightProvider(capabilities.getDocumentHighlightProvider());
        asp.setDocumentLinkProvider(capabilities.getDocumentLinkProvider());
        asp.setDocumentOnTypeFormattingProvider(capabilities.getDocumentOnTypeFormattingProvider());
        asp.setDocumentRangeFormattingProvider(capabilities.getDocumentRangeFormattingProvider());
        asp.setDocumentSymbolProvider(capabilities.getDocumentSymbolProvider());
        asp.setExecuteCommandProvider(capabilities.getExecuteCommandProvider());
        asp.setExperimental(capabilities.getExperimental());
        asp.setFoldingRangeProvider(capabilities.getFoldingRangeProvider());
        asp.setHoverProvider(capabilities.getHoverProvider());
        asp.setImplementationProvider(capabilities.getImplementationProvider());
        asp.setReferencesProvider(capabilities.getReferencesProvider());
        asp.setRenameProvider(capabilities.getRenameProvider());
        asp.setSemanticTokensProvider(capabilities.getSemanticTokensProvider());
        asp.setSignatureHelpProvider(capabilities.getSignatureHelpProvider());
        asp.setTextDocumentSync(capabilities.getTextDocumentSync());
        asp.setTypeDefinitionProvider(capabilities.getTypeDefinitionProvider());
        asp.setWorkspace(capabilities.getWorkspace());
        asp.setWorkspaceSymbolProvider(capabilities.getWorkspaceSymbolProvider());
        this.capabilities = asp;
    }
}
