package cz.craftmania.craftvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import cz.craftmania.craftvelocity.api.proxycheck.ProxyCheckAPI;
import cz.craftmania.craftvelocity.commands.admin.GlobalAdminChatCommand;
import cz.craftmania.craftvelocity.commands.autologin.AutologinAdminCommand;
import cz.craftmania.craftvelocity.commands.autologin.AutologinCommand;
import cz.craftmania.craftvelocity.commands.internal.EventServerTpCommand;
import cz.craftmania.craftvelocity.listeners.*;
import cz.craftmania.craftvelocity.managers.AutologinManager;
import cz.craftmania.craftvelocity.managers.CraftTaskManager;
import cz.craftmania.craftvelocity.sql.SQLManager;
import cz.craftmania.craftvelocity.tasks.ConnectionWhitelistUpdateTask;
import cz.craftmania.craftvelocity.tasks.JoinRateLimitResetterTask;
import cz.craftmania.craftvelocity.tasks.PlayerUpdateTask;
import cz.craftmania.craftvelocity.utils.Config;
import dev.mayuna.pumpk1n.Pumpk1n;
import dev.mayuna.pumpk1n.impl.FolderStorageHandler;
import lombok.Getter;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "craftvelocity",
        name = "CraftVelocity",
        version = BuildConstants.VERSION,
        url = "https://craftmania.cz",
        authors = {"Mayuna"}
)
public class Main {

    // Plugin
    private static @Getter Main instance;
    private @Getter Config config;

    // Velocity
    private @Inject @Getter Logger logger;
    private @Inject @Getter ProxyServer server;
    private @Inject @Getter @DataDirectory Path dataDirectory;

    // Managers
    private @Getter CraftTaskManager craftTaskManager;
    private @Getter SQLManager sqlManager;
    private @Getter AutologinManager autologinManager;
    private @Getter Pumpk1n pumpk1n;

    // Channels
    public final static String CRAFTEVENTS_CHANNEL = "craftevents:plugin"; // Channel pro zasilani notifikaci pro zacatek eventu

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        long start = System.currentTimeMillis();
        instance = this;

        logger.info("CraftVelocity @ " + BuildConstants.VERSION);
        logger.info("Starting up...");

        logger.info("Loading config(s)...");
        loadConfiguration();

        logger.info("Loading SQL database...");
        loadSQL();

        logger.info("Loading managers...");
        loadManagers();

        logger.info("Loading tasks...");
        loadTasks();

        logger.info("Loading listeners...");
        loadListeners();

        logger.info("Loading commands...");
        loadCommands();

        logger.info("Finished loading! Took " + (System.currentTimeMillis() - start) + "ms");
    }


    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        logger.info("Shutting down CraftVelocity @ " + BuildConstants.VERSION);

        logger.info("Finished shutting down. o/");
    }

    private void loadConfiguration() {
        config = new Config(dataDirectory);
        config.loadFile();
        config.loadConfig();

        ProxyCheckAPI.setApiKey(config.getProxyCheck().getApiKey());
    }

    private void loadSQL() {
        sqlManager = new SQLManager();
        sqlManager.createAutologinPlayersTable();
    }

    private void loadManagers() {
        craftTaskManager = new CraftTaskManager();

        autologinManager = new AutologinManager();
        autologinManager.init();

        pumpk1n = new Pumpk1n(new FolderStorageHandler(config.getPumpk1n().getDataFolder()));
        pumpk1n.prepareStorage();
    }

    private void loadTasks() {
        craftTaskManager.register(new ConnectionWhitelistUpdateTask());
        craftTaskManager.register(new JoinRateLimitResetterTask());
        craftTaskManager.register(new PlayerUpdateTask());
    }

    private void loadListeners() {
        EventManager eventManager = server.getEventManager();

        // Autologin
        eventManager.register(this, new AutologinConnectionListener());

        // CraftBungee rewrite
        eventManager.register(this, new EventNotifyListener());
        eventManager.register(this, new HelpCommandListener());
        eventManager.register(this, new PlayerListener());
        eventManager.register(this, new VoteListener());
        eventManager.register(this, new VPNListener());
        eventManager.register(this, new BlacklistedNamesListener());
    }

    private void loadCommands() {
        CommandManager commandManager = server.getCommandManager();

        // Autologin
        new AutologinAdminCommand().registerCommand(commandManager);
        new AutologinCommand().registerCommand(commandManager);

        // GlobalAdminChat
        new GlobalAdminChatCommand().registerCommand(commandManager);

        // Internal
        new EventServerTpCommand().registerCommand(commandManager);
    }
}
