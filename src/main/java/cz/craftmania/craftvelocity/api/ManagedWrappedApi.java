package cz.craftmania.craftvelocity.api;

import cz.craftmania.craftvelocity.Main;
import dev.mayuna.simpleapi.WrappedApi;

import java.net.http.HttpClient;
import java.time.Duration;

public abstract class ManagedWrappedApi implements WrappedApi {

    private HttpClient httpClient;

    @Override
    public HttpClient createHttpClientInstance() {
        if (httpClient == null) {
            httpClient = WrappedApi.super.createHttpClientInstance();
        }

        return httpClient;
    }

    @Override
    public Duration getTimeoutDuration() {
        return Duration.ofMillis(Main.getInstance().getConfig().getApi().getTimeoutMillis());
    }
}
