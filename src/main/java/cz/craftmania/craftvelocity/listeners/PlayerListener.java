package cz.craftmania.craftvelocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.Main;

public class PlayerListener {

    @Subscribe
    public void onLogin(PostLoginEvent event) {
        Player player = event.getPlayer();

        Main.getInstance().getSqlManager().updateStats(player, true);
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();

        Main.getInstance().getSqlManager().updateStats(player, false);
    }
}
