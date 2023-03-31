package cz.craftmania.craftvelocity.listeners;

import com.mojang.brigadier.tree.CommandNode;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.utils.Config;
import cz.craftmania.craftvelocity.utils.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public class HelpCommandListener {

    @Subscribe
    public void onPlayerAvailableCommandsEvent(PlayerAvailableCommandsEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("craftvelocity.completions.whitelist")) {
            return;
        }

        //TODO: Ccominuty profile nastavení.
        if (player.hasPermission("craftvelocity.completions.blacklist")) {
            event.getRootNode().getChildren().clear();
            return;
        }

        event.getRootNode().getChildren().removeIf(commandNode -> !checkCommand(player, commandNode));
    }

    /**
     * Checks if player can see the command in tab completion (checks by completions group in config)
     * @param player Player
     * @param commandNode Command node
     * @return Indicates if player will be able to see the command in tab completions
     */
    private boolean checkCommand(Player player, CommandNode<?> commandNode) {
        String label = commandNode.getName();
        AtomicBoolean isAllowed = new AtomicBoolean(false); // Default blokace všeho!
        Config config = Main.getInstance().getConfig();

        // Pokud je na default whitelistu vzdy povoleno!
        if (config.getHelpCommands().getDefaults().contains(label)) {
            return true;
        }

        config.getHelpCommands().getGroups().forEach((groupName, groupData) -> {
            if (player.hasPermission("craftvelocity.completions.group." + groupName)) {
                Logger.debug("Processing completions group '" + groupName + "' {" + groupData + "} for " + player.getUsername());

                // Hráč má příkaz na seznamu
                if (groupData.has(label)) {

                    // Hráč má příkaz na seznamu a seznam je nastavený na blacklist
                    if (!groupData.isWhitelist()) {
                        // Zablokujeme tab complete
                        isAllowed.set(false);
                    } else {
                        // Hráč má příkaz na seznamu a seznam je nastavený na whitelist
                        isAllowed.set(true);
                    }
                } else {
                    // Seznam je nastavený na whitelist - jen příkazy v seznamu jsou povolené
                    if (groupData.isWhitelist()) {
                        // Hráč nemá příkaz na seznamu ALE je nastavený na whitelist -> allowed
                        isAllowed.set(true);
                    } else {
                        isAllowed.set(false);
                    }
                }
            }
        });

        return isAllowed.get();
    }
}
