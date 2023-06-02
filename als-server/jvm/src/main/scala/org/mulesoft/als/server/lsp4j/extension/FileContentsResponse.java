package org.mulesoft.als.server.lsp4j.extension;

import java.util.Map;

public class FileContentsResponse {
    private Map<String, String> fs;

    public FileContentsResponse(Map<String, String> fs) {
        this.fs = fs;
    }

    public Map<String, String> getFs() { return fs; }
}
