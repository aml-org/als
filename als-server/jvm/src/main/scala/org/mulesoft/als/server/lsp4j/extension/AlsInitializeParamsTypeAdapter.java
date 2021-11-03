package org.mulesoft.als.server.lsp4j.extension;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.eclipse.lsp4j.adapters.InitializeParamsTypeAdapter;
import org.eclipse.lsp4j.generator.TypeAdapterImpl;

import java.io.IOException;

@TypeAdapterImpl(AlsInitializeParams.class)
public class AlsInitializeParamsTypeAdapter extends InitializeParamsTypeAdapter {

    private Gson gson;
    public AlsInitializeParamsTypeAdapter(Gson gson) {
        super(gson);
        this.gson = gson;
    }

    @SuppressWarnings("all")
    public static class Factory implements TypeAdapterFactory {
        public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
            if (!AlsInitializeParams.class.isAssignableFrom(typeToken.getRawType())) {
                return null;
            }
            return (TypeAdapter<T>) new AlsInitializeParamsTypeAdapter(gson);
        }
    }

    @Override
    protected AlsClientCapabilities readCapabilities(JsonReader in) throws IOException {
        return gson.fromJson(in, AlsClientCapabilities.class);
    }

    protected AlsConfiguration readConfiguration(JsonReader in) throws IOException {
        return gson.fromJson(in, AlsConfiguration.class);
    }
    @Override
    public AlsInitializeParams read(final JsonReader in) throws IOException {
        JsonToken nextToken = in.peek();
        if (nextToken == JsonToken.NULL) {
            return null;
        }

        AlsInitializeParams result = new AlsInitializeParams();
        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "processId":
                    result.setProcessId(readProcessId(in));
                    break;
                case "rootPath":
                    result.setRootPath(readRootPath(in));
                    break;
                case "rootUri":
                    result.setRootUri(readRootUri(in));
                    break;
                case "initializationOptions":
                    result.setInitializationOptions(readInitializationOptions(in));
                    break;
                case "capabilities":
                    result.setCapabilities(readCapabilities(in));
                    break;
                case "clientName":
                    result.setClientName(readClientName(in));
                    break;
                case "trace":
                    result.setTrace(readTrace(in));
                    break;
                case "workspaceFolders":
                    result.setWorkspaceFolders(readWorkspaceFolders(in));
                    break;
                case "configuration":
                    result.setConfiguration(readConfiguration(in));
                    break;
                case "projectConfigurationStyle":
                    result.setProjectConfigurationStyle(readProjectConfiguration(in));
                default:
                    in.skipValue();
            }
        }
        in.endObject();
        return result;
    }

    private ProjectConfigurationStyle readProjectConfiguration(JsonReader in) {
        return gson.fromJson(in, ProjectConfigurationStyle.class);
    }
}
