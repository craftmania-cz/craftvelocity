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

    public static void error(String message, Exception exception) {
        Main.getInstance().getLogger().error(message, exception);
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
}
