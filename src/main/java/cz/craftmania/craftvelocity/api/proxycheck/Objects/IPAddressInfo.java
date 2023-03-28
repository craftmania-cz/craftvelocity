package cz.craftmania.craftvelocity.api.proxycheck.Objects;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Holds information about IP addres and its information such as ASN, ISOCODE, proxy yes/no, type and risk value
 */
public class IPAddressInfo {

    private @Getter @Setter String ip;
    private @Getter String asn;
    private @Getter String isocode;
    private @Getter String proxy;
    private @Getter String type;
    private @Getter int risk;

     /**
     * Checks if field proxy is equal to `yes`
     * @return Returns true if proxy is equal to `yes`
     */
    public boolean isProxy() {
        return Objects.equals(proxy, "yes");
    }

    /**
     * Returns ISO Code
     * @return ISO Code
     */
    public String getCountryCode() {
        return isocode;
    }
}
