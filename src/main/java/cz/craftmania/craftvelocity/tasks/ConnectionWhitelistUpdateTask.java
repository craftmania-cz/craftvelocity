package cz.craftmania.craftvelocity.tasks;

import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.listeners.BlacklistedNamesListener;
import cz.craftmania.craftvelocity.listeners.VPNListener;
import cz.craftmania.craftvelocity.sql.SQLManager;
import cz.craftmania.craftvelocity.utils.Logger;

public class ConnectionWhitelistUpdateTask implements CraftTaskTimer {

    @Override
    public String getName() {
        return "ConnectionWhitelistUpdateTask";
    }

    @Override
    public long getDelay() {
        return Main.getInstance().getConfig().getConnectionWhitelist().getUpdater().getDelayMillis();
    }

    @Override
    public long getInterval() {
        return Main.getInstance().getConfig().getConnectionWhitelist().getUpdater().getIntervalMillis();
    }

    @Override
    public boolean isFixed() {
        return true;
    }

    @Override
    public void run() {
        Logger.connectionWhitelist("Aktualizuji Connection Whitelist listy (IP, NICK, ASN, Allowed blacklisted nicks, blacklisted nick words)...");

        SQLManager sqlManager = Main.getInstance().getSqlManager();

        sqlManager.fetchWhitelistedIPs().whenComplete(((fetchedWhitelistedIPs, throwable) -> {
            if (throwable == null) {
                var whitelistedIPs = VPNListener.getWhitelistedIPs();

                synchronized (whitelistedIPs) {
                    whitelistedIPs.clear();
                    whitelistedIPs.addAll(fetchedWhitelistedIPs);
                }

                Logger.connectionWhitelist("Proběhla aktualizace whitelisted IPs: " + fetchedWhitelistedIPs.size() + " IPs");
            }
        }));

        sqlManager.fetchWhitelistedNames().whenComplete(((fetchedWhitelistedNames, throwable) -> {
            if (throwable == null) {
                var whitelistedNames = VPNListener.getWhitelistedNames();

                synchronized (whitelistedNames) {
                    whitelistedNames.clear();
                    whitelistedNames.addAll(fetchedWhitelistedNames);
                }

                Logger.connectionWhitelist("Proběhla aktualizace whitelisted nicků: " + fetchedWhitelistedNames.size() + " nicků");
            }
        }));

        sqlManager.fetchBlacklistedASNs().whenComplete(((fetchedBlacklistedASNs, throwable) -> {
            if (throwable == null) {
                var blacklistedASNs = VPNListener.getBlacklistedASNs();

                synchronized (blacklistedASNs) {
                    blacklistedASNs.clear();
                    blacklistedASNs.addAll(fetchedBlacklistedASNs);
                }

                Logger.connectionWhitelist("Proběhla aktualizace blacklisted ASNs: " + fetchedBlacklistedASNs.size() + " ASNs");
            }
        }));

        sqlManager.fetchAllowedBlacklistedNicks().whenComplete((fetchedWhitelistedNicks, throwable) -> {
            if (throwable == null) {
                var whitelistedNicks = BlacklistedNamesListener.getWhitelistedNicks();

                synchronized (whitelistedNicks) {
                    whitelistedNicks.clear();
                    whitelistedNicks.addAll(fetchedWhitelistedNicks);
                }

                Logger.connectionWhitelist("Proběhla aktualizace povolených zablokováných nicků: " + fetchedWhitelistedNicks.size() + " nicků");
            }
        });

        sqlManager.fetchBlacklistedWords().whenComplete((fetchedBlacklistedWords, throwable) -> {
            if (throwable == null) {
                var blacklistedWords = BlacklistedNamesListener.getBlacklistedWords();

                synchronized (blacklistedWords) {
                    blacklistedWords.clear();
                    blacklistedWords.addAll(fetchedBlacklistedWords);
                }

                Logger.connectionWhitelist("Proběhla aktualizace zablokovaných slov v nicku: " + fetchedBlacklistedWords.size() + " slov");
            }
        });

        Logger.connectionWhitelist("Aktualizace Connection Whitelist listů byla dokončena.");
    }
}
