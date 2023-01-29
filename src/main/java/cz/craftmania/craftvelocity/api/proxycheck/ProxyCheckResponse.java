package cz.craftmania.craftvelocity.api.proxycheck;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.craftmania.craftvelocity.api.proxycheck.Objects.ProxyCheckResult;
import cz.craftmania.craftvelocity.api.proxycheck.gson.ProxyCheckResultTypeAdapter;
import dev.mayuna.simpleapi.APIResponse;
import dev.mayuna.simpleapi.deserializers.GsonDeserializer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public abstract class ProxyCheckResponse extends APIResponse<ProxyCheckAPI> implements GsonDeserializer {

    private @Getter @Setter String status;
    private @Getter @Setter String message;

    public boolean isOk() {
        return status.equals("ok");
    }

    @Override
    public @NonNull Gson getGson() {
        return new GsonBuilder().registerTypeAdapter(ProxyCheckResult.class, new ProxyCheckResultTypeAdapter()).create();
    }
}
