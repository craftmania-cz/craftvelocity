package cz.craftmania.craftvelocity.objects.connectionwhitelist;

/**
 * Holds information for whitelisted nick
 * @param nick Player's nick
 * @param description Description to why the nick was whitelisted (usually non-informative)
 */
public record WhitelistedName(String nick, String description) {

}
