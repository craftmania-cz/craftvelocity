package cz.craftmania.craftvelocity.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.api.craftmania.CraftManiaAPI;
import cz.craftmania.craftvelocity.api.craftmania.objects.PlayerInfo;
import cz.craftmania.craftvelocity.api.proxycheck.Objects.IPAddressInfo;
import cz.craftmania.craftvelocity.api.proxycheck.Objects.ProxyCheckResult;
import cz.craftmania.craftvelocity.api.proxycheck.ProxyCheckAPI;
import cz.craftmania.craftvelocity.objects.connectionwhitelist.BlacklistedASN;
import cz.craftmania.craftvelocity.objects.connectionwhitelist.WhitelistedIP;
import cz.craftmania.craftvelocity.objects.connectionwhitelist.WhitelistedName;
import cz.craftmania.craftvelocity.utils.Logger;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class VPNListener {

    private static final @Getter List<WhitelistedName> whitelistedNames = Collections.synchronizedList(new LinkedList<>());
    private static final @Getter List<WhitelistedIP> whitelistedIPs = Collections.synchronizedList(new LinkedList<>());
    private static final @Getter List<BlacklistedASN> blacklistedASNs = Collections.synchronizedList(new LinkedList<>());

    // Chceme tuto kontrolu nechat jako poslední, jelikož dotazuje free tier proxychecku
    @Subscribe(order = PostOrder.LAST)
    public void onPreLogin(PreLoginEvent event) {
        if (!event.getResult().isAllowed()) {
            return;
        }

        String playerAddress = event.getConnection().getRemoteAddress().getAddress().getHostAddress();
        String username = event.getUsername();

        Logger.vpn("Kontrola hráče " + username + " (" + playerAddress + ")");

        AtomicBoolean is403 = new AtomicBoolean(false);
        boolean vpn = false;

        var request = ProxyCheckAPI.getInstance().fetchProxyCheck("185.115.9.195");

        ProxyCheckResult result;
        IPAddressInfo ipAddressInfo;

        try {
            result = request.sendAsync().join();
        } catch (Exception exception) {
            Logger.vpnError("Nastala chyba při kontrole IP hráče " + username + " (" + playerAddress + ")! Hráč bude propuštěn bez kontroly...", exception);
            return;
        }

        if (result.getHttpStatusCode() == 403) {
            Logger.vpnWarning("ProxyCheck navrátil HTTP Response s kodem 403 - Hráč pustím bez kontroly.");
            return;
        }

        if (!result.isOk()) {
            Logger.vpnError("ProxyCheck navrátil neplatný výsledek - Hráč bude propuštěn bez kontroly. (" + result.getStatus() + " [" + result.getMessage() + "]");
            return;
        }

        ipAddressInfo = result.getIpAddressInfo();

        if (ipAddressInfo == null) {
            Logger.vpnWarning("IP Address Info při kontrole IP hráče " + username + " (" + playerAddress + ") je null - nelze zkontrolovat. Propouštím hráče bez kontroly...");
            return;
        }

        // Check whitelisted names
        WhitelistedName whitelistedName;
        synchronized (whitelistedNames) {
            whitelistedName = whitelistedNames.stream().filter(x -> x.nick().equals(username)).findAny().orElse(null);
        }
        if (whitelistedName != null) {
            Logger.vpn("Hráč " + username + " (" + playerAddress + ") má whitelisted nick (důvod: " + whitelistedName.description() + ") - Bude propuštěn na server");
            return;
        }

        // Check whitelisted ips
        WhitelistedIP whitelistedIP;
        synchronized (whitelistedIPs) {
            whitelistedIP = whitelistedIPs.stream().filter(x -> x.address().matcher(playerAddress).matches()).findAny().orElse(null);
        }
        if (whitelistedIP != null) {
            Logger.vpn("Hráč " + username + " (" + playerAddress + ") má whitelisted IP (whitelisted IP pattern: '" + whitelistedIP.address().toString() + "', důvod: " + whitelistedIP.description() + ") - Bude propuštěn na server");
            return;
        }

        // Check blacklisted ASNs
        String playerASN = ipAddressInfo.getAsn();
        BlacklistedASN blacklistedASN;
        synchronized (blacklistedASNs) {
            blacklistedASN = blacklistedASNs.stream().filter(x -> x.asn().equalsIgnoreCase(playerASN)).findAny().orElse(null);
        }
        if (blacklistedASN != null) {
            try {
                PlayerInfo.PlayerInfoData data = CraftManiaAPI.getInstance().fetchPlayerInfo(username).sendAsync().join().getData();

                if (data.getPlayedTime() <= 60) {
                    Logger.vpnWarning("Hráč " + username + " (" + playerAddress + ") má zablokovaného poskytovatele a nemá nahráno více jak 1h na serveru - jeho připojení bylo zablokováno.");
                    event.setResult(PreLoginEvent.PreLoginComponentResult.denied(Component.text(Main.getInstance().getConfig().getProxyCheck().getMessages().getBlockedASN())));
                    return;
                }
            } catch (Exception exception) {
                Logger.vpnError("Při kontrole hráčovo play time nastal exception! Hráč podstoupí další kontrole.", exception);
            }
        }

        // Proxy = Je VPN
        if (ipAddressInfo.isProxy()) {
            vpn = true;
        }

        // ISOCODE = null, chováme se k němu jako VPN
        if (ipAddressInfo.getIsocode() == null) {
            vpn = true;
        }

        String stateIso = ipAddressInfo.getIsocode();

        if (stateIso.equalsIgnoreCase("CZ") || stateIso.equalsIgnoreCase("SK")) {
            if (vpn) {
                Logger.vpnWarning("Hráč " + username + " (" + playerAddress + ") má CZ/SK VPN a není na address/name whitelistu - jeho připojení bylo zablokováno.");
                event.setResult(PreLoginEvent.PreLoginComponentResult.denied(Component.text(Main.getInstance().getConfig().getProxyCheck().getMessages().getVpn())));
                return;
            }

            Logger.vpn("Hráč " + username + " (" + playerAddress + ") pochází z CZ / SK - Bude propuštěn na server");
        } else {
            Logger.vpnWarning("Hráč " + username + " (" + playerAddress + ") má IP Addressu z jiné země než CZ/SK (vpn: " + vpn + ") - jeho připojení bylo zablokováno.");
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(Component.text(Main.getInstance().getConfig().getProxyCheck().getMessages().getForeignIP())));
            return;
        }

        // Pokud se sem dostane kod, hráč byl propušten
    }
}
