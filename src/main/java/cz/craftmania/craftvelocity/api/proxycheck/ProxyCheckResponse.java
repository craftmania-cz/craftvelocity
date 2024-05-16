package cz.craftmania.craftvelocity.api.proxycheck;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.craftmania.craftvelocity.api.proxycheck.Objects.ProxyCheckResult;
import cz.craftmania.craftvelocity.api.proxycheck.gson.ProxyCheckResultTypeAdapter;
import dev.mayuna.simpleapi.GsonApiResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public abstract class ProxyCheckResponse extends GsonApiResponse<ProxyCheckAPI> {

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(ProxyCheckResult.class, new ProxyCheckResultTypeAdapter()).create();

    private @Getter @Setter String status;
    private @Getter @Setter String message;

    public boolean isOk() {
        return status == null || status.equals("ok");
    }

    @Override
    public @NonNull Gson getGson() {
        return GSON;
    }
}
