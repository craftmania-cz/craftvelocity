package cz.craftmania.craftvelocity.utils;

import com.moandjiezana.toml.Toml;
import cz.craftmania.craftvelocity.Main;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class Config {

    private final @Getter Plugin plugin = new Plugin();
    private final @Getter Autologin autologin = new Autologin();
    private final @Getter SQL sql = new SQL();
    private final @Getter Pumpk1n pumpk1n = new Pumpk1n();

    private Path dataDirectory = null;
    private Toml tomlFile = null;

    public Config(Path path) {
        dataDirectory = path;
    }

    public void loadFile() {
        File dataDirectory = this.dataDirectory.toFile();
        if (!dataDirectory.exists()) {
            if (!dataDirectory.mkdirs()) {
                Main.getInstance().getLogger().error("Failed to create folders for config data directory " + this.dataDirectory);
                return;
            }
        }

        File dataFile = new File(dataDirectory, "config.toml");
        if (!dataFile.exists()) {
            try {
                InputStream inputStream = getClass().getResourceAsStream("/config.toml");
                if (inputStream != null) {
                    Files.copy(inputStream, dataFile.toPath());
                } else {
                    throw new RuntimeException("ERROR: Can't read default configuration file (permissions/filesystem error?)");
                }
            } catch (IOException e) {
                throw new RuntimeException("ERROR: Can't write default configuration file (permissions/filesystem error?)");
            }
        }

        this.tomlFile = new Toml().read(dataFile);

        Main.getInstance().getLogger().info("Config was read.");
    }

    public void loadConfig() {
        plugin.debug = tomlFile.getBoolean("plugin.debug", false);

        autologin.cache.invalidateMineToolsCacheAfter = tomlFile.getLong("autologin.cache.invalidateMineToolsCacheAfter", 1800000L);
        autologin.cache.invalidateAutologinCacheAfter = tomlFile.getLong("autologin.cache.invalidateAutologinCacheAfter", 3600000L);
        autologin.cache.invalidateDisabledAutologinCacheAfter = tomlFile.getLong("autologin.cache.invalidateDisabledAutologinCacheAfter", 3600000L);

        autologin.messages.invalidNick = tomlFile.getString("autologin.messages.invalidNick", "INVALID_NICK");
        autologin.messages.invalidToken = tomlFile.getString("autologin.messages.invalidToken", "INVALID_TOKEN");
        autologin.messages.authServerNotFound = tomlFile.getString("autologin.messages.authServerNotFound", "AUTH_SERVER_NOT_FOUND");
        autologin.messages.databaseError = tomlFile.getString("autologin.messages.databaseError", "DATABASE_ERROR");
        autologin.messages.runtimeError = tomlFile.getString("autologin.messages.runtimeError", "RUNTIME_ERROR");
        autologin.messages.autologinEnabled = tomlFile.getString("autologin.messages.autologinEnabled", "AUTOLOGIN_ENABLED");
        autologin.messages.autologinDisabled = tomlFile.getString("autologin.messages.autologinDisabled", "AUTOLOGIN_DISABLED");
        autologin.messages.autologinEnabledForced = tomlFile.getString("autologin.messages.autologinEnabledForced", "AUTOLOGIN_ENABLED_FORCED");
        autologin.messages.autologinDisabledForced = tomlFile.getString("autologin.messages.autologinDisabledForced", "AUTOLOGIN_DISABLED_FORCED");

        autologin.servers.auth = tomlFile.getString("autologin.servers.auth", "whub");
        autologin.servers.lobbies = tomlFile.getList("autologin.servers.lobbies", new LinkedList<>());

        sql.hostname = tomlFile.getString("sql.hostname", "hostname");
        sql.database = tomlFile.getString("sql.database", "database");
        sql.username = tomlFile.getString("sql.username", "username");
        sql.password = tomlFile.getString("sql.password", "password");

        sql.settings.minimumConnections = tomlFile.getLong("sql.settings.minimumConnections", 2L);
        sql.settings.maximumConnections = tomlFile.getLong("sql.settings.maximumConnections", 6L);
        sql.settings.timeout = tomlFile.getLong("sql.settings.timeout", 30000L);

        pumpk1n.dataFolder = tomlFile.getString("pumpk1n.dataFolder", "./pumpk1n/");

        Main.getInstance().getLogger().info("Config was loaded.");
    }

    public static class Plugin {

        private @Getter boolean debug = false;

    }

    public static class Autologin {

        private final @Getter Cache cache = new Cache();
        private final @Getter Messages messages = new Messages();
        private final @Getter Servers servers = new Servers();

        public static class Cache {

            private @Getter long invalidateMineToolsCacheAfter;
            private @Getter long invalidateAutologinCacheAfter;
            private @Getter long invalidateDisabledAutologinCacheAfter;
        }

        public static class Messages {

            private @Getter String invalidNick;
            private @Getter String invalidToken;
            private @Getter String authServerNotFound;
            private @Getter String databaseError;
            private @Getter String runtimeError;
            private @Getter String autologinEnabled;
            private @Getter String autologinDisabled;
            private @Getter String autologinEnabledForced;
            private @Getter String autologinDisabledForced;
        }

        public static class Servers {

            private @Getter String auth;
            private @Getter List<String> lobbies;
        }
    }

    public static class SQL {

        private final @Getter Settings settings = new Settings();

        private @Getter String hostname;
        private @Getter String database;
        private @Getter String username;
        private @Getter String password;

        public static class Settings {

            private @Getter long minimumConnections;
            private @Getter long maximumConnections;
            private @Getter long timeout;
        }
    }

    public static class Pumpk1n {

        private @Getter String dataFolder;
    }
}
