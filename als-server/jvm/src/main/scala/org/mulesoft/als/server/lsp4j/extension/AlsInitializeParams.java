package org.mulesoft.als.server.lsp4j.extension;

import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.InitializeParams;

public class AlsInitializeParams extends InitializeParams {

    private AlsClientCapabilities alsClientCapabilities;

    @Override
    public AlsClientCapabilities getCapabilities() {
        return alsClientCapabilities;
    }

    public void setCapabilities(AlsClientCapabilities capabilities) {
        alsClientCapabilities = capabilities;
    }

    @Override
    public void setCapabilities(ClientCapabilities capabilities) {
        AlsClientCapabilities acp = new AlsClientCapabilities();
        acp.setTextDocument(capabilities.getTextDocument());
        acp.setWorkspace(capabilities.getWorkspace());
        acp.setExperimental(capabilities.getExperimental());
        alsClientCapabilities = acp;
    }
}