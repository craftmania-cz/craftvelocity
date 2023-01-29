package cz.craftmania.craftvelocity.api.proxycheck.Objects;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class IPAddressInfo {

    private @Getter @Setter String ip;
    private @Getter String asn;
    private @Getter String isocode;
    private @Getter String proxy;
    private @Getter String type;
    private @Getter int risk;

    public boolean isProxy() {
        return Objects.equals(proxy, "yes");
    }

    public String getCountryCode() {
        return isocode;
    }
}
