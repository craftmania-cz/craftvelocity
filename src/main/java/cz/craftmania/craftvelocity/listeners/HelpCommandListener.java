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
        if (player.hasPermission("craftbungee.completions.blacklist")) {
            event.getRootNode().getChildren().clear();
            return;
        }

        event.getRootNode().getChildren().removeIf(commandNode -> !checkCommand(player, commandNode));
    }

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
                Logger.debug("Processing group '" + groupName + "' {" + groupData + "} for " + player.getUsername());

                // Pokud je prikaz na seznamu, bude povolen, jinak blokovan
                if (!groupData.has(label)) {
                    isAllowed.set(false);
                } else {
                    isAllowed.set(true);
                }
            }
        });

        return isAllowed.get();
    }
}
