package cz.craftmania.craftvelocity

import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import cz.craftmania.craftvelocity.utils.Config
import lombok.Getter
import java.nio.file.Path

@Plugin(id = "craftvelocity", name = "CraftVelocity", version = "1.0-SNAPSHOT")
class Main {

    @Getter private var server: ProxyServer? = null
    @Getter private var logger: Logger? = null
    @Getter private var dataDirectory: Path? = null
    var config: Config? = null

    @Inject
    constructor(server: ProxyServer, logger: Logger, @DataDirectory dataDirectory: Path) {
        this.server = server
        this.logger = logger
        this.dataDirectory = dataDirectory

        logger.info("CraftVelocity loading...")
        this.config = Config(this.dataDirectory!!)
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {

    }
}