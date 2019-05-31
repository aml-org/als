package org.mulesoft.als.server.custom;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.mulesoft.lsp.textsync.DidFocusParams;

public interface CustomEvents {
    @JsonNotification
    void didFocus(DidFocusParams params);
}
