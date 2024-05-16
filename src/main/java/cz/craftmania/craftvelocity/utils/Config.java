package cz.craftmania.craftvelocity.utils;

import com.moandjiezana.toml.Toml;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.objects.GroupData;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Config {

    private final @Getter Plugin plugin = new Plugin();
    private final @Getter Autologin autologin = new Autologin();
    private final @Getter SQL sql = new SQL();
    private final @Getter API api = new API();
    private final @Getter ProxyCheck proxyCheck = new ProxyCheck();
    private final @Getter ConnectionWhitelist connectionWhitelist = new ConnectionWhitelist();
    private final @Getter NickBlacklist nickBlacklist = new NickBlacklist();
    private final @Getter JoinRateLimit joinRateLimit = new JoinRateLimit();
    private final @Getter PlayerUpdateTask playerUpdateTask = new PlayerUpdateTask();
    private final @Getter GHelp gHelp = new GHelp();
    private final @Getter Pumpk1n pumpk1n = new Pumpk1n();
    private final @Getter HelpCommands helpCommands = new HelpCommands();
    private final @Getter Vote vote = new Vote();
    private final @Getter KickGuard kickGuard = new KickGuard();

    private Path dataDirectory = null;
    private Toml tomlFile = null;

    public Config(Path path) {
        dataDirectory = path;
    }

    /**
     * Loads config file
     */
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

    /**
     * Loads config values from loaded config. {@link #loadFile()} must be called before this method invocation.
     */
    public void loadConfig() {
        plugin.debug = tomlFile.getBoolean("plugin.debug", false);

        autologin.cache.invalidateMineToolsCacheAfter = tomlFile.getLong("autologin.cache.invalidateMineToolsCacheAfter", 1800000L);
        autologin.cache.invalidateAutologinCacheAfter = tomlFile.getLong("autologin.cache.invalidateAutologinCacheAfter", 3600000L);
        autologin.cache.invalidateDisabledAutologinCacheAfter = tomlFile.getLong("autologin.cache.invalidateDisabledAutologinCacheAfter", 3600000L);

        autologin.messages.authServerNotFound = tomlFile.getString("autologin.messages.authServerNotFound", "AUTH_SERVER_NOT_FOUND");
        autologin.messages.databaseError = tomlFile.getString("autologin.messages.databaseError", "DATABASE_ERROR");
        autologin.messages.runtimeError = tomlFile.getString("autologin.messages.runtimeError", "RUNTIME_ERROR");
        autologin.messages.autologinEnabled = tomlFile.getString("autologin.messages.autologinEnabled", "AUTOLOGIN_ENABLED");
        autologin.messages.autologinDisabled = tomlFile.getString("autologin.messages.autologinDisabled", "AUTOLOGIN_DISABLED");
        autologin.messages.autologinEnabledForced = tomlFile.getString("autologin.messages.autologinEnabledForced", "AUTOLOGIN_ENABLED_FORCED");
        autologin.messages.autologinDisabledForced = tomlFile.getString("autologin.messages.autologinDisabledForced", "AUTOLOGIN_DISABLED_FORCED");

        autologin.servers.auth = tomlFile.getString("autologin.servers.auth", "whub");
        autologin.servers.lobbies = tomlFile.getList("autologin.servers.lobbies", new LinkedList<>());
        autologin.database.proxyDatabaseName = tomlFile.getString("autologin.database.proxyDatabase", "bungeecord.");
        autologin.database.minigamesDatabaseName = tomlFile.getString("autologin.database.minigamesDatabase", "minigames.");

        sql.hostname = tomlFile.getString("sql.hostname", "hostname");
        sql.database = tomlFile.getString("sql.database", "database");
        sql.username = tomlFile.getString("sql.username", "username");
        sql.password = tomlFile.getString("sql.password", "password");

        sql.settings.minimumConnections = tomlFile.getLong("sql.settings.minimumConnections", 2L);
        sql.settings.maximumConnections = tomlFile.getLong("sql.settings.maximumConnections", 6L);
        sql.settings.timeout = tomlFile.getLong("sql.settings.timeout", 30000L);

        api.timeoutMillis = tomlFile.getLong("api.timeoutMillis", 5000L);

        proxyCheck.apiKey = tomlFile.getString("proxycheck.apiKey", "Proxycheck API Key");
        proxyCheck.messages.vpn = tomlFile.getString("proxycheck.messages.vpn", "IP_IS_VPN_ERROR");
        proxyCheck.messages.foreignIP = tomlFile.getString("proxycheck.messages.foreignIP", "IP_IS_NOT_CZ_OR_SK_ERROR");
        proxyCheck.messages.blockedASN = tomlFile.getString("proxycheck.messages.blockedASN", "BLOCKED_ASN_ERROR");

        connectionWhitelist.updater.delayMillis = tomlFile.getLong("connectionwhitelist.updater.delayMillis", 10_000L);
        connectionWhitelist.updater.intervalMillis = tomlFile.getLong("connectionwhitelist.updater.intervalMillis", 60_000L);

        nickBlacklist.messages.blacklistedWords = tomlFile.getString("nickblacklist.messages.blacklistedWords", "BLACKLISTED_WORDS_IN_NICK_ERROR");

        joinRateLimit.joinLimit = tomlFile.getLong("joinratelimit.joinLimit", 10L);
        joinRateLimit.delayMillis = tomlFile.getLong("joinratelimit.delayMillis", 0L);
        joinRateLimit.intervalMillis = tomlFile.getLong("joinratelimit.intervalMillis", 300L);
        joinRateLimit.messages.limitReached = tomlFile.getString("joinratelimit.messages.limitReached", "JOIN_RATE_LIMIT_REACHED");

        playerUpdateTask.delayMillis = tomlFile.getLong("playerupdatetask.delayMillis", 60000L);
        playerUpdateTask.intervalMillis = tomlFile.getLong("playerupdatetask.intervalMillis", 60000L);

        gHelp.cooldownMillis = tomlFile.getLong("ghelp.cooldownMillis", 60000L);

        pumpk1n.dataFolder = tomlFile.getString("pumpk1n.dataFolder", "./pumpk1n/");

        helpCommands.load(tomlFile);

        vote.voteServers = tomlFile.getList("vote.voteServers", new LinkedList<>());
        vote.voteTokens.amount = (int)(long)tomlFile.getLong("vote.votetokens.amount");

        kickGuard.enabled = tomlFile.getBoolean("kickGuard.enabled", false);
        kickGuard.whitelistedServers = tomlFile.getList("kickGuard.whitelistedServers", new ArrayList<>());
        kickGuard.messages.noKickReason = tomlFile.getString("kickGuard.messages.noKickReason");
        kickGuard.messages.kickedMessage = tomlFile.getString("kickGuard.messages.kickedMessage");

        Main.getInstance().getLogger().info("Config was loaded.");
    }

    public static class Plugin {
        private @Getter boolean debug = false;
    }

    public static class Autologin {
        private final @Getter Cache cache = new Cache();
        private final @Getter Messages messages = new Messages();
        private final @Getter Servers servers = new Servers();
        private final @Getter Database database = new Database();

        public static class Cache {
            private @Getter long invalidateMineToolsCacheAfter;
            private @Getter long invalidateAutologinCacheAfter;
            private @Getter long invalidateDisabledAutologinCacheAfter;
        }

        public static class Messages {
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

        public static class Database {
            private @Getter String proxyDatabaseName;
            private @Getter String minigamesDatabaseName;
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

    public static class API {
        private @Getter long timeoutMillis;
    }

    public static class ProxyCheck {
        private final @Getter Messages messages = new Messages();
        private @Getter String apiKey;

        public static class Messages{
            private @Getter String vpn;
            private @Getter String foreignIP;
            private @Getter String blockedASN;
        }
    }

    public static class ConnectionWhitelist {
        private final @Getter Updater updater = new Updater();

        public static class Updater {
            private @Getter long delayMillis;
            private @Getter long intervalMillis;
        }
    }

    public static class NickBlacklist {
        private final @Getter Messages messages = new Messages();

        public static class Messages {
            private @Getter String blacklistedWords;
        }
    }

    public static class JoinRateLimit {
        private final @Getter Messages messages = new Messages();
        private @Getter long joinLimit;
        private @Getter long delayMillis;
        private @Getter long intervalMillis;

        public static class Messages {
            private @Getter String limitReached;
        }
    }

    public static class PlayerUpdateTask {

        private @Getter long delayMillis;
        private @Getter long intervalMillis;
    }

    public static class GHelp {

        private @Getter long cooldownMillis;
    }

    public static class Pumpk1n {

        private @Getter String dataFolder;
    }

    public static class HelpCommands {

        private @Getter boolean defaultBlacklist;
        private final @Getter List<String> defaults = new LinkedList<>();
        private final @Getter Map<String, GroupData> groups = new HashMap<>();

        public void load(Toml tomlFile) {
            defaultBlacklist = tomlFile.getBoolean("help-commands.defaults.blacklist", false);
            defaults.addAll(tomlFile.getList("help-commands.defaults.completions", new LinkedList<>()));

            Toml tomlGroups = tomlFile.getTable("help-commands.groups");

            tomlGroups.toMap().forEach((groupName, value) -> {
                String tableName = "help-commands.groups." + groupName;

                boolean isWhitelist = !tomlFile.getBoolean(tableName + ".blacklist", defaultBlacklist);
                List<String> completions = tomlFile.getList(tableName + ".completions", new LinkedList<>());

                groups.put(groupName, new GroupData(completions, isWhitelist));
            });
        }
    }

    public static class Vote {

        private @Getter List<String> voteServers;
        private @Getter VoteTokens voteTokens = new VoteTokens();

        public static class VoteTokens {

            private @Getter int amount;
        }
    }

    public static class KickGuard {

        private @Getter Messages messages = new Messages();
        private @Getter boolean enabled;
        private @Getter List<String> whitelistedServers = new ArrayList<>();

        public static class Messages {

            private @Getter String kickedMessage;
            private @Getter String noKickReason;
        }

    }
}
