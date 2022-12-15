package cz.craftmania.craftvelocity.managers;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.utils.Logger;
import net.kyori.adventure.text.Component;

public class AutologinManager {

    public void init() {

    }

    public void processPreLoginEvent(PreLoginEvent event, Continuation continuation) {
        String username = event.getUsername();

        try {
            Logger.info("[AUTOLOGIN] Zpracovávám hráče " + username);

            Main.getInstance().getSqlManager().fetchAutologinPlayer(username).whenCompleteAsync((autologinPlayer, throwable) -> {
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

                continuation.resume();
            });
        } catch (Exception exception) {
            Logger.error("[AUTOLOGIN] Nastala chyba při zpracovávání hráče " + username + "! Hráč bude vykopnut.", exception);
            String errorMessage = Main.getInstance().getConfig().getAutologin().getMessages().getRuntimeError();
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(Component.text(errorMessage)));
            continuation.resume();
        }
    }
}
