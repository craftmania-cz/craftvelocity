package cz.craftmania.craftvelocity.api.proxycheck;

import cz.craftmania.craftvelocity.api.ManagedWrappedApi;
import cz.craftmania.craftvelocity.api.proxycheck.Objects.ProxyCheckResult;
import dev.mayuna.simpleapi.ApiRequest;
import dev.mayuna.simpleapi.PathParameter;
import dev.mayuna.simpleapi.RequestMethod;
import lombok.NonNull;
import lombok.Setter;

/**
 * Wraps ProxyCheck's API into a {@link ManagedWrappedApi}
 */
public class ProxyCheckAPI extends ManagedWrappedApi {

    private static ProxyCheckAPI instance;
    private static @Setter String apiKey;

    public static ProxyCheckAPI getInstance() {
        if (instance == null) {
            instance = new ProxyCheckAPI();
        }

        return instance;
    }

    @Override
    public @NonNull String getDefaultUrl() {
        return "https://proxycheck.io";
    }

    /**
     * Fetches {@link ProxyCheckResult} from ProxyCheck's API
     * @param ipAddress IP address
     * @return {@link ApiRequest} with {@link ProxyCheckResult}
     */
    public ApiRequest<ProxyCheckResult> fetchProxyCheck(String ipAddress) {
        return ApiRequest.builder(this, ProxyCheckResult.class)
                .withEndpoint("/v2/{ip}?key={apikey}&vpn=1&asn=1&node=1&time=1&inf=0&risk=1&port=1&seen=1&days=7&tag=Bungeecord")
                .withRequestMethod(RequestMethod.GET)
                .withPathParameter(PathParameter.of("ip", ipAddress))
                .withPathParameter(PathParameter.of("apikey", apiKey))
                .build();
    }
}
