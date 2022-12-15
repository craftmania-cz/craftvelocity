package cz.craftmania.craftvelocity.listeners;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.utils.Logger;
import net.kyori.adventure.text.Component;

public class AutologinConnectionListener {

    @Subscribe
    public void onPlayerPreLogin(PreLoginEvent event, Continuation continuation) {
        Main.getInstance().getAutologinManager().processPreLoginEvent(event, continuation);
    }

    @Subscribe
    public void onPlayerChooseInitialServer(PlayerChooseInitialServerEvent event) {
        Player player = event.getPlayer();

        if (!event.getPlayer().isOnlineMode()) {
            String authServer = Main.getInstance().getConfig().getAutologin().getServers().getAuth();

            Logger.info("[AUTOLOGIN] Hráč " + player.getUsername() + " (" + player.getUniqueId() + ") je offline hráč. Posílám ho na auth server '" + authServer + "'!");

            RegisteredServer server = Main.getInstance().getServer().getServer(authServer).orElse(null);

            if (server == null) {
                Logger.warning("[AUTOLOGIN] Auth server " + authServer + " není registrovaný ve velocity! Hráče " + player.getUsername() + " kickuji...");
                player.disconnect(Component.text(Main.getInstance().getConfig().getAutologin().getMessages().getAuthServerNotFound()));
                return;
            }

            event.setInitialServer(server);
        }
    }
}
