package org.mulesoft.als.server.lsp4j.extension;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.ClientInfo;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.adapters.InitializeParamsTypeAdapter;
import org.eclipse.lsp4j.generator.TypeAdapterImpl;

import java.io.IOException;
import java.util.Optional;

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
                case "clientInfo":
                    result.setClientInfo(readClientInfo(in));
                    break;
                case "clientName":
                    Optional.ofNullable(result.getClientInfo())
                            .ifPresent(clientInfo -> {
                                try {
                                    result.setClientInfo(new ClientInfo(readClientInfo(in).getName(), clientInfo.getVersion()));
                                } catch (IOException e) {
                                    // Do nothing, maybe log the exception
                                }
                            });
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
                case "hotReload":
                    result.setHotReload(readHotReload(in));
                    break;
                case "newCachingLogic":
                    result.setNewCachingLogic(readNewCachingLogic(in));
                    break;
                default:
                    in.skipValue();
            }
        }
        in.endObject();
        return result;
    }

    private Boolean readHotReload(JsonReader in) {
        return gson.fromJson(in, Boolean.class);
    }

    private Boolean readNewCachingLogic(JsonReader in) {
        return gson.fromJson(in, Boolean.class);
    }

    @Override
    protected void writeCapabilities(final JsonWriter out, final ClientCapabilities value) throws IOException {
        gson.toJson(value,  AlsClientCapabilities.class, out);
    }

}
