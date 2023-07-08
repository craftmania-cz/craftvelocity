package cz.craftmania.craftvelocity.managers;

import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.List;
import java.util.Random;

public class KickGuardManager {

    private static final Random random = new Random();

    public void handleKickedFromServerEvent(KickedFromServerEvent event) {
        if (!Main.getInstance().getConfig().getKickGuard().isEnabled()) {
            return;
        }

        Player player = event.getPlayer();
        RegisteredServer kickedFromServer = event.getServer();
        String kickedFromServerName = kickedFromServer.getServerInfo().getName();

        if (!Main.getInstance().getConfig().getKickGuard().getWhitelistedServers().contains(kickedFromServerName)) {
            // Server is not whitelisted in kickGuard.whitelistedServers - ignore
            return;
        }

        List<String> lobbies = Main.getInstance().getConfig().getAutologin().getServers().getLobbies();

        // No lobbies set
        if (lobbies.size() == 0) {
            Logger.kickGuardError("There are no lobbies in config 'autologin.servers.lobbies'! Cannot send kicked player to lobby server...");
            return;
        }

        // Choose random lobby server
        String lobbyServerName = lobbies.get(random.nextInt(lobbies.size()));

        RegisteredServer lobbyServer = Main.getInstance().getServer().getServer(lobbyServerName).orElse(null);

        if (lobbyServer == null) {
            Logger.kickGuardError("Lobby server with name '" + lobbyServerName + "' does not exist in Velocity! Cannot send kicked player to lobby server...");
            return;
        }

        if (player.getCurrentServer().isPresent()) {
            // Player is already connected to the lobby server, no need to redirect...
            if (player.getCurrentServer().get().getServerInfo().getName().equals(lobbyServer.getServerInfo().getName())) {
                return;
            }
        }

        String kickMessage = Main.getInstance().getConfig().getKickGuard().getMessages().getKickedMessage()
                                         .replace("{server}", kickedFromServerName);

        if (event.getServerKickReason().isPresent() && event.getServerKickReason().get() instanceof TextComponent textComponent) {
            kickMessage = kickMessage.replace("{kick_reason}", textComponent.content());
        } else {
            kickMessage = kickMessage.replace("{kick_reason}", Main.getInstance().getConfig().getKickGuard().getMessages().getNoKickReason());
        }

        Logger.kickGuardWarning("Player " + player.getUsername() + " (" + player.getUniqueId() + ") was kicked from server '" + kickedFromServerName + "' - Redirecting them to lobby server '" + lobbyServerName + "'...");
        event.setResult(KickedFromServerEvent.RedirectPlayer.create(lobbyServer, Component.text(kickMessage)));
    }
}
