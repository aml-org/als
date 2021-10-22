package org.mulesoft.als.server.lsp4j.internal;

import com.google.gson.GsonBuilder;
import org.mulesoft.als.server.lsp4j.extension.AlsInitializeParamsTypeAdapter;

import java.util.function.Consumer;

public class GsonConsumerBuilder implements Consumer<GsonBuilder> {
    @Override
    public void accept(GsonBuilder gsonBuilder) {
        gsonBuilder
           .registerTypeAdapterFactory(new AlsInitializeParamsTypeAdapter.Factory());
    }
}
