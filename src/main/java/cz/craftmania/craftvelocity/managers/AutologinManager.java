package cz.craftmania.craftvelocity.managers;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.api.minetools.MineToolsAPI;
import cz.craftmania.craftvelocity.api.minetools.objects.MineToolsPlayer;
import cz.craftmania.craftvelocity.cache.AutologinCache;
import cz.craftmania.craftvelocity.objects.AutologinPlayer;
import cz.craftmania.craftvelocity.utils.Logger;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AutologinManager {

    private final @Getter AutologinCache cache = new AutologinCache();

    public void init() {
        cache.init();
    }

    public void processPreLoginEvent(PreLoginEvent event, Continuation continuation) {
        String username = event.getUsername();

        try {
            Logger.info("[AUTOLOGIN] Zpracovávám hráče " + username);


            fetchAutologinPlayer(username).whenCompleteAsync((autologinPlayer, throwable) -> {
                if (throwable != null) {
                    String errorMessage = Main.getInstance().getConfig().getAutologin().getMessages().getDatabaseError();

                    event.setResult(PreLoginEvent.PreLoginComponentResult.denied(Component.text(errorMessage)));
                    continuation.resumeWithException(throwable);
                    return;
                }

                if (autologinPlayer != null) {
                    Logger.info("[AUTOLOGIN] Hráč " + username + " má zapnutý autologin! Propouštím ho jako originálku...");
                    Logger.debug("[AUTOLOGIN] Hráč " + username + " je viděn jako premium. UUID z databáze: " + autologinPlayer.getUuid() + " Poslední připojení: " + autologinPlayer.getLastOnline());
                    event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
                }

                // Pokud je hráč null, tak nic neforcujeme. Necháme to na defaultním nastavení proxyny.

                continuation.resume();
            });
        } catch (Exception exception) {
            Logger.error("[AUTOLOGIN] Nastala chyba při zpracovávání hráče " + username + "! Hráč bude vykopnut.", exception);
            String errorMessage = Main.getInstance().getConfig().getAutologin().getMessages().getRuntimeError();
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(Component.text(errorMessage)));
            continuation.resume();
        }
    }

    public CompletableFuture<AutologinPlayer> fetchAutologinPlayer(String nick) {
        CompletableFuture<AutologinPlayer> completableFuture = new CompletableFuture<>();

        cache.fetchOrLoadAutologinPlayerFromNick(nick).whenCompleteAsync(((autologinPlayer, throwable) -> {
            if (throwable != null) {
                completableFuture.completeExceptionally(throwable);
                return;
            }

            completableFuture.complete(autologinPlayer);
        }));

        return completableFuture;
    }

    /**
     * Tries to enable autologin for nickname.<br>
     *
     * The return will be null if:<br>
     * - Player is non-premium<br>
     * - Player in MineToolsCache has mismatched nicks with the passed nick into this method<br>
     * <br>
     * Throwable indicates that there was an exception when processing the MineTools API or anything else.
     *
     * @param nick Nick of the player
     * @return CompletableFuture<AutologinPlayer>
     */
    public CompletableFuture<AutologinPlayer> enableAutologin(String nick) {
        CompletableFuture<AutologinPlayer> completableFuture = new CompletableFuture<>();

        cache.fetchOrLoadMineToolsPlayerFromNick(nick)
             .whenCompleteAsync((autologinCacheObject, throwable) -> {
                 if (throwable != null) {
                     completableFuture.completeExceptionally(throwable);
                     return;
                 }

                 if (!autologinCacheObject.isOriginalNick()) { // Nick není originální
                     completableFuture.complete(null);
                     return;
                 }

                 MineToolsPlayer player = autologinCacheObject.getMineToolsPlayer();

                 if (!player.isNickSame(nick)) { // WAKED_ != Waked_
                     completableFuture.complete(null);
                     return;
                 }

                 AutologinPlayer autologinPlayer = new AutologinPlayer(player.getUUID(), nick);

                 Main.getInstance().getSqlManager().insertOrUpdateAutologinPlayer(autologinPlayer).whenCompleteAsync((aVoid, throwableSql) -> {
                     if (throwableSql != null) {
                         completableFuture.completeExceptionally(throwableSql);
                         return;
                     }

                     cache.forceAddToAutologinPlayerCache(autologinPlayer);
                     cache.invalidateDisabledAutologinCacheForNick(autologinPlayer.getNick());
                     completableFuture.complete(autologinPlayer); // Autologin byl povolený
                 });
             });

        return completableFuture;
    }

    public CompletableFuture<AutologinPlayer> enableAutologin(String nick, UUID uuid) {
        CompletableFuture<AutologinPlayer> completableFuture = new CompletableFuture<>();

        AutologinPlayer autologinPlayer = new AutologinPlayer(uuid, nick);

        Main.getInstance().getSqlManager().insertOrUpdateAutologinPlayer(autologinPlayer).whenCompleteAsync(((unused, throwableSql) -> {
            if (throwableSql != null) {
                completableFuture.completeExceptionally(throwableSql);
                return;
            }

            cache.invalidateAutologinCacheForNick(nick);
            cache.invalidateMineToolsCacheForNick(nick);
            cache.invalidateDisabledAutologinCacheForNick(nick);
            cache.forceAddToAutologinPlayerCache(autologinPlayer);

            completableFuture.complete(autologinPlayer);
        }));

        return completableFuture;
    }

    public CompletableFuture<Void> disableAutologin(String nick) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        Main.getInstance().getSqlManager().removeAutologinPlayer(nick).whenCompleteAsync((aVoid, throwable) -> {
            if (throwable != null) {
                completableFuture.completeExceptionally(throwable);
                return;
            }

            cache.invalidateAutologinCacheForNick(nick);
            cache.forceAddToDisabledAutologinPlayerCache(nick);

            completableFuture.complete(aVoid);
        });

        return completableFuture;
    }
}
