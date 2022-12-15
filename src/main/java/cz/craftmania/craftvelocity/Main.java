package cz.craftmania.craftvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import cz.craftmania.craftvelocity.utils.Config;
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

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        long start = System.currentTimeMillis();
        instance = this;

        logger.info("CraftVelocity @ " + BuildConstants.VERSION);
        logger.info("Starting up...");

        logger.info("Loading config(s)...");
        loadConfiguration();

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
    }
}
