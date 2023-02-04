package cz.craftmania.craftvelocity.api.proxycheck.Objects;

import cz.craftmania.craftvelocity.api.proxycheck.ProxyCheckResponse;
import lombok.Getter;

public class ProxyCheckResult extends ProxyCheckResponse {

    private @Getter IPAddressInfo ipAddressInfo;
    private @Getter String queryTime;

    public ProxyCheckResult(IPAddressInfo ipAddressInfo, String queryTime) {
        this.ipAddressInfo = ipAddressInfo;
        this.queryTime = queryTime;
    }
}