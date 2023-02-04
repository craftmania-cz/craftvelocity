package cz.craftmania.craftvelocity.utils;

import cz.craftmania.craftvelocity.Main;

import java.sql.SQLException;

public class Logger {

    public static void info(String message) {
        Main.getInstance().getLogger().info(message);
    }

    public static void warning(String message) {
        Main.getInstance().getLogger().warn( message);
    }

    public static void error(String message) {
        Main.getInstance().getLogger().error(message);
    }

    public static void error(String message, Throwable throwable) {
        Main.getInstance().getLogger().error(message, throwable);
    }

    public static void debug(String message) {
        Config config = Main.getInstance().getConfig();

        if (config != null) {
            if (!config.getPlugin().isDebug()) {
                return;
            }
        }

        Main.getInstance().getLogger().info("[DEBUG] " + message);
    }

    public static void sql(String message) {
        Main.getInstance().getLogger().info("[SQL] " + message);
    }

    public static void debugSQL(String message) {
        debug("[SQL] " + message);
    }

    public static void sql(String message, SQLException exception) {
        Main.getInstance().getLogger().error("[SQL] " + message, exception);
    }

    public static void vote(String message) {
        info("[HLASOVANI] " + message);
    }

    public static void vpn(String message) {
        info("[VPN] " + message);
    }

    public static void vpnWarning(String message) {
        warning("[VPN] " + message);
    }

    public static void vpnError(String message) {
        error("[VPN] " + message);
    }

    public static void vpnError(String message, Throwable throwable) {
        error("[VPN] " + message, throwable);
    }

    public static void nickBlacklist(String message) {
        info("[NICK-BLACKLIST] " + message);
    }

    public static void nickBlacklistDebug(String message) {
        debug("[NICK-BLACKLIST] " + message);
    }

    public static void connectionWhitelist(String message) {
        info("[CONNECTION-WHITELIST-UPDATER] " + message);
    }
}
