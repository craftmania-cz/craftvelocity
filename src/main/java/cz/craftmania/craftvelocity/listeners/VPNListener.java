package cz.craftmania.craftvelocity.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import cz.craftmania.craftvelocity.api.proxycheck.ProxyCheckAPI;
import cz.craftmania.craftvelocity.utils.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public class VPNListener {

    @Subscribe(order = PostOrder.LATE)
    public void onPreLogin(PreLoginEvent event) {
        if (!event.getResult().isAllowed()) {
            return;
        }

        String playerAddress = event.getConnection().getRemoteAddress().getAddress().getHostAddress();
        String username = event.getUsername();

        Logger.vpn("Kontrola hráče " + username + " (" + playerAddress + ")");

        AtomicBoolean is403 = new AtomicBoolean(false);
        AtomicBoolean allowPlayer = new AtomicBoolean(false);

        var action = ProxyCheckAPI.getInstance().fetchProxyCheck(playerAddress);

        action.onHttpError(httpError -> {
            if (httpError.getCode() == 403) {
                is403.set(true);
            }
        });

        action.execute().whenComplete(((proxyCheckResult, throwable) -> {
            if (throwable != null) {
                if (is403.get()) {
                    Logger.vpn("ProxyCheck navrátil HTTP Response s kodem 403 - Hráč pustím bez kontroly.");
                    allowPlayer.set(true);
                    return;
                }

                Logger.vpn("Nastala chyba při kontrole IP hráče " + username + " (" + playerAddress + ")!", throwable);
                return;
            }


        }));
    }
}
