package cz.craftmania.craftvelocity.api.proxycheck.Objects;

import cz.craftmania.craftvelocity.api.proxycheck.ProxyCheckResponse;

public class ProxyCheckResult extends ProxyCheckResponse {

    private IPAddressInfo ipAddressInfo;
    private String queryTime;

    public ProxyCheckResult(IPAddressInfo ipAddressInfo, String queryTime) {
        this.ipAddressInfo = ipAddressInfo;
        this.queryTime = queryTime;
    }
}
