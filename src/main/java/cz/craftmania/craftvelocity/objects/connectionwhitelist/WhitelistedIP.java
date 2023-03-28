package cz.craftmania.craftvelocity.objects.connectionwhitelist;

import java.util.regex.Pattern;

/**
 * Holds info about whitelisted IP
 * @param address IP Address in REGEX pattern
 * @param description Description to why the IP was whitelisted (usually non-informative)
 */
public record WhitelistedIP(Pattern address, String description) {

}
