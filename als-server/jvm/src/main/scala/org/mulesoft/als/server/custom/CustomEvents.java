package org.mulesoft.als.server.custom;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.mulesoft.als.server.protocol.textsync.DidFocusParams;

public interface CustomEvents {
    @JsonNotification
    void didFocus(DidFocusParams params);
}
