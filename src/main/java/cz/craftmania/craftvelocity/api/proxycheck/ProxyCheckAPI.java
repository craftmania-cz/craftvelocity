package cz.craftmania.craftvelocity.api.proxycheck;

import cz.craftmania.craftvelocity.api.proxycheck.Objects.ProxyCheckResult;
import dev.mayuna.simpleapi.APIRequest;
import dev.mayuna.simpleapi.Action;
import dev.mayuna.simpleapi.PathParameter;
import dev.mayuna.simpleapi.SimpleAPI;
import lombok.NonNull;
import lombok.Setter;

public class ProxyCheckAPI extends SimpleAPI {

    private static ProxyCheckAPI instance;
    private static @Setter String apiKey;

    public static ProxyCheckAPI getInstance() {
        if (instance == null) {
            instance = new ProxyCheckAPI();
        }

        return instance;
    }

    @Override
    public @NonNull String getURL() {
        return "https://proxycheck.io";
    }

    public Action<ProxyCheckResult> fetchProxyCheck(String ipAddress) {
        return new Action<>(this, ProxyCheckResult.class, new APIRequest.Builder()
                .setEndpoint("/v2/{ip}?key={apikey}&vpn=1&asn=1&node=1&time=1&inf=0&risk=1&port=1&seen=1&days=7&tag=Bungeecord")
                .setMethod("GET")
                .addPathParameter(new PathParameter("ip", ipAddress))
                .addPathParameter(new PathParameter("apikey", apiKey))
                .build());
    }
}
