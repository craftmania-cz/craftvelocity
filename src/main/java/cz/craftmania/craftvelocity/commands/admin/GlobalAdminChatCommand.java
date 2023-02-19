package cz.craftmania.craftvelocity.commands.admin;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.commands.CraftCommand;
import cz.craftmania.craftvelocity.utils.ChatInfo;
import cz.craftmania.craftvelocity.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class GlobalAdminChatCommand implements CraftCommand {

    private final List<UUID> disabledPlayers = Collections.synchronizedList(new LinkedList<>());

    @Override
    public String getCommandAlias() {
        return "ga";
    }

    @Override
    public String[] getCommandAliases() {
        return new String[]{"admin-chat", "global-admin-chat"};
    }

    @Override
    public String[] getPermissionNodes() {
        return new String[]{"craftvelocity.at-chat"};
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource commandSource = invocation.source();
        String[] arguments = invocation.arguments();

        // /ga
        if (arguments.length == 0) {
            ChatInfo.error(commandSource, "Invalidní syntax příkazu. Syntax: /ga <'toggle'|zpráva>");
            return;
        }

        if (arguments.length == 1) {
            String action = arguments[0];

            // /ga toggle
            if (action.equalsIgnoreCase("toggle")) {
                if (!(commandSource instanceof Player player)) {
                    ChatInfo.error(commandSource, "Tento subcommand příkazu /ga je jen pro hráče!");
                    return;
                }

                UUID playerUUID = player.getUniqueId();

                if (disabledPlayers.contains(playerUUID)) {
                    disabledPlayers.remove(playerUUID);

                    ChatInfo.success(player, "Nyní se ti budou zobrazovat zprávy z Globálního Admin Chatu.");

                } else {
                    disabledPlayers.add(playerUUID);

                    ChatInfo.success(player, "Nyní se ti §cnebudou{c} zobrazovat zprávy z Globálního Admin Chatu.");
                }

                return;
            }
        }

        // /ga zpráva pro ostatní AT členy

        String playerNick = "CONSOLE";
        String playerServer = "N/A";

        if (commandSource instanceof Player player) {
            playerNick = player.getUsername();

            if (player.getCurrentServer().isPresent()) {
                playerServer = player.getCurrentServer().get().getServer().getServerInfo().getName();
            }

        }

        Component message = Component.text("§4§lGACHAT §a" + playerNick + "§7: ")
                .hoverEvent(HoverEvent.showText(Component.text("§7Server: §e" + playerServer + "\n\n§7Kliknutím se připojíš")))
                .clickEvent(ClickEvent.runCommand("/server " + playerServer))
                .append(Component.text("§e" + String.join(" ", arguments)));

        Logger.info("[GACHAT] " + playerNick + ": " + String.join(" ", arguments));

        Main.getInstance().getServer().getAllPlayers().forEach(player -> {
            boolean hasPermission = false;

            for (String permissionNode : getPermissionNodes()) {
                if (player.hasPermission(permissionNode)) {
                    hasPermission = true;
                    break;
                }
            }

            if (!hasPermission) {
                return;
            }

            if (disabledPlayers.contains(player.getUniqueId())) {
                return;
            }

            player.sendMessage(message);
        });
    }
}
