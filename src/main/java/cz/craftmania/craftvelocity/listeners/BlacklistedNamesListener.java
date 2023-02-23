package cz.craftmania.craftvelocity.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.utils.Logger;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BlacklistedNamesListener {

    private static final @Getter List<String> whitelistedNicks = Collections.synchronizedList(new LinkedList<>());
    private static final @Getter List<String> blacklistedWords = Collections.synchronizedList(new LinkedList<>());

    @Subscribe(order = PostOrder.LATE)
    public void onPreLogin(PreLoginEvent event) {
        if (!event.getResult().isAllowed()) {
            return;
        }

        String username = event.getUsername();

        Logger.nickBlacklistDebug("Kontrola hráče s nickem " + username);

        synchronized (blacklistedWords) {
            String matchedWord = blacklistedWords.stream().filter(word -> username.toLowerCase().contains(word.toLowerCase())).findAny().orElse(null);

            if (matchedWord == null) {
                Logger.nickBlacklistDebug("Kontrola hráče s nickem " + username + " proběhla úspěšně");
                return;
            }

            synchronized (whitelistedNicks) {
                boolean matchesAny = whitelistedNicks.stream().anyMatch(nick -> nick.equalsIgnoreCase(username));

                if (!matchesAny) {
                    event.setResult(PreLoginEvent.PreLoginComponentResult.denied(Component.text(Main.getInstance().getConfig().getNickBlacklist().getMessages().getBlacklistedWords())));
                    Logger.nickBlacklist("Hráčův nick " + username + " obsahuje zablokované slovo " + matchedWord + " - jeho připojení bylo zablokováno.");
                } else {
                    Logger.nickBlacklistDebug("Kontrola hráče s nickem " + username + " proběhla úspěšně - slovo " + matchedWord + " je zablokované, ale jeho nick je v nick whitelistu.");
                }
            }
        }
    }
}
