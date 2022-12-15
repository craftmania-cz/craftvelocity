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

        autologin.connectivity.connectionTimeout = tomlFile.getLong("autologin.connectivity.connectionTimeout", 3000L);
        autologin.connectivity.readTimeout = tomlFile.getLong("autologin.connectivity.readTimeout", 3000L);

        autologin.messages.invalidNick = tomlFile.getString("autologin.messages.invalidNick", "INVALID_NICK");
        autologin.messages.invalidToken = tomlFile.getString("autologin.messages.invalidToken", "INVALID_TOKEN");

        autologin.servers.auth = tomlFile.getString("autologin.servers.auth", "whub");
        autologin.servers.lobbies = tomlFile.getList("autologin.servers.lobbies", new LinkedList<>());

        sql.hostname = tomlFile.getString("sql.hostname", "hostname");
        sql.database = tomlFile.getString("sql.database", "database");
        sql.username = tomlFile.getString("sql.username", "username");
        sql.password = tomlFile.getString("sql.password", "password");

        sql.settings.minimumConnections = tomlFile.getLong("sql.settings.minimumConnections", 2L);
        sql.settings.maximumConnections = tomlFile.getLong("sql.settings.maximumConnections", 6L);
        sql.settings.timeout = tomlFile.getLong("sql.settings.timeout", 30000L);

        Main.getInstance().getLogger().info("Config was loaded.");
    }

    private static class Plugin {

        private @Getter boolean debug = false;

    }

    private static class Autologin {

        private final @Getter Connectivity connectivity = new Connectivity();
        private final @Getter Messages messages = new Messages();
        private final @Getter Servers servers = new Servers();

        private static class Connectivity {

            private @Getter long connectionTimeout;
            private @Getter long readTimeout;
        }

        private static class Messages {

            private @Getter String invalidNick;
            private @Getter String invalidToken;
        }

        private static class Servers {

            private @Getter String auth;
            private @Getter List<String> lobbies;
        }
    }

    private static class SQL {

        private final @Getter Settings settings = new Settings();

        private @Getter String hostname;
        private @Getter String database;
        private @Getter String username;
        private @Getter String password;

        private static class Settings {

            private @Getter long minimumConnections;
            private @Getter long maximumConnections;
            private @Getter long timeout;
        }
    }
}
