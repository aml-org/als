package org.mulesoft.als.server.lsp4j.extension;

import java.util.List;

public class ConversionServerOptions {
    private List<ConversionConf> supported;

    public ConversionServerOptions(List<ConversionConf> supported) {
        this.supported = supported;
    }

    public List<ConversionConf> getSupported() {
        return supported;
    }

    public void setSupported(List<ConversionConf> supported) {
        this.supported = supported;
    }
}
