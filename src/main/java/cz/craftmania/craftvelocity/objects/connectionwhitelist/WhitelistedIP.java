package cz.craftmania.craftvelocity.objects.connectionwhitelist;

import java.util.regex.Pattern;

public record WhitelistedIP(Pattern address, String description) {

}
