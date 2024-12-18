package cz.craftmania.craftvelocity.listeners;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.api.minetools.MineToolsAPI;
import cz.craftmania.craftvelocity.data.PlayerIgnoredAutologinMessageData;
import cz.craftmania.craftvelocity.managers.AutologinManager;
import cz.craftmania.craftvelocity.objects.AutologinPlayer;
import cz.craftmania.craftvelocity.utils.ChatInfo;
import cz.craftmania.craftvelocity.utils.Logger;
import dev.mayuna.pumpk1n.objects.DataHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class AutologinConnectionListener {

    @Subscribe(order = PostOrder.LAST)
    public void onPlayerPreLogin(PreLoginEvent event, Continuation continuation) {
        if (!event.getResult().isAllowed()) {
            return;
        }

        Main.getInstance().getAutologinManager().processPreLoginEvent(event, continuation);
    }

    @Subscribe
    public void onPlayerChooseInitialServer(PlayerChooseInitialServerEvent event) {
        Player player = event.getPlayer();

        // Warez hub
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
        } else {
            // Hrač má originalku, musí být přesunut na lobby
            String originalLoginLobbyName = Main.getInstance().getConfig().getAutologin().getServers().getLobbies().get(0); // Random?
            RegisteredServer originalLoginServer = Main.getInstance().getServer().getServer(originalLoginLobbyName).orElse(null);
            if (originalLoginServer == null) {
                Logger.warning("[AUTOLOGIN] Auth server " + originalLoginServer + " není registrovaný ve velocity! Hráče " + player.getUsername() + " kickuji...");
                player.disconnect(Component.text(Main.getInstance().getConfig().getAutologin().getMessages().getAuthServerNotFound()));
                return;
            }
            event.setInitialServer(originalLoginServer);
        }
    }

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        Player player = event.getPlayer();

        Main.getInstance().getAutologinManager().fetchAutologinPlayer(player.getUsername()).whenCompleteAsync(((autologinPlayer, throwable) -> {
            if (throwable != null) {
                Logger.error("Nastala chyba při načítání AutologinPlayer pro nick " + player.getUsername(), throwable);
                return;
            }

            if (autologinPlayer != null) {
                // Hráč již má zapnutý autologin
                return;
            }

            DataHolder playerDataHolder = Main.getInstance().getPumpk1n().getDataHolder(player.getUniqueId());

            if (playerDataHolder != null) {
                PlayerIgnoredAutologinMessageData data = playerDataHolder.getDataElement(PlayerIgnoredAutologinMessageData.class);

                if (data != null) {
                    return;
                }
            }

            MineToolsAPI.getInstance().getMineToolsPlayer(player.getUsername()).sendAsync().whenCompleteAsync(((mineToolsPlayer, throwableMineTools) -> {
                if (throwableMineTools != null) {
                    Logger.error("Nastala chyba při získávání dat z MineToolsAPI pro nick " + player.getUsername(), throwableMineTools);
                    return;
                }

                if (mineToolsPlayer.isOriginalNick()) {
                    ChatInfo.info(player, "Vypadá to, že tvůj nick §e" + player.getUsername() + "{c} je originální. Pokud vlastníš originální Minecraft, měl by sis zapnout funkci §eautologin");
                    player.sendMessage(Component.text("§7>> Autologin tě bude automaticky přihlašovat bez potřeby zadání hesla. §aTím si zvýšíš bezpečnost svého účtu."));
                    player.sendMessage(Component.text());
                    ChatInfo.error(player, "Pozor! Pokud nevlastníš originální Minecraft a zapneš si autologin, nebudeš se moct na tvůj nick připojit.");
                    player.sendMessage(Component.text());

                    TextComponent autologinEnableComponent = Component.text(ChatInfo.infoMessage("Pokud vlastníš originální Minecraft a chceš si §azapnout{c} autologin, "))
                                                                      .append(Component.text("§eklikni zde")
                                                                                       .hoverEvent(HoverEvent.showText(Component.text("§cPokud nevlastníš originální Minecraft, §4§lneklikej!")))
                                                                                       .clickEvent(ClickEvent.suggestCommand("/autologin on")));

                    TextComponent autologinIgnoreComponent = Component.text(ChatInfo.infoMessage("Pokud tuto zprávu již §cnechceš{c} vidět, "))
                                                                      .append(Component.text("§eklikni zde")
                                                                                       .hoverEvent(HoverEvent.showText(Component.text("§7Pokud by sis to rozmyslel, pro zapnutí autologinu použij §e/autologin on")))
                                                                                       .clickEvent(ClickEvent.suggestCommand("/autologin ignore")));

                    player.sendMessage(autologinEnableComponent);
                    player.sendMessage(autologinIgnoreComponent);
                }
            }));
        }));
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();

        Main.getInstance().getAutologinManager().fetchAutologinPlayer(player.getUsername()).whenCompleteAsync(((autologinPlayer, throwable) -> {
            if (throwable != null) {
                Logger.error("[AUTOLOGIN] Nebylo možné aktualizovat hodnotu lastOnline pro hráče " + player.getUsername() + " (" + player.getUniqueId() + ")!", throwable);
                return;
            }

            Logger.info("[AUTOLOGIN] Aktualizuji lastOnline hodnotu pro hráče " + player.getUsername() + " (" + player.getUniqueId() + ")...");

            autologinPlayer.updateLastOnline();
            autologinPlayer.updateOnSQL();
        }));
    }
}
