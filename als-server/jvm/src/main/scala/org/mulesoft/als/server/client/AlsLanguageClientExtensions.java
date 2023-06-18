package org.mulesoft.als.server.client;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageClientExtensions;
import org.mulesoft.als.server.feature.serialization.SerializationResult;
import org.mulesoft.als.server.feature.workspace.FilesInProjectParams;

import java.io.StringWriter;

public interface AlsLanguageClientExtensions extends LanguageClientExtensions {
    /**
     * Serialization notifications are sent from the server to the client to
     * signal results of serialization runs.
     */
    @JsonNotification("serializeJSONLD")
    void publishSerialization(SerializationResult<StringWriter> serialization);

    /**
     * filesInProject notifications are sent from the server to the client to
     * signal results of main file runs.
     */
    @JsonNotification("filesInProject")
    void publishProjectFiles(FilesInProjectParams filesInProjectParams);
}
