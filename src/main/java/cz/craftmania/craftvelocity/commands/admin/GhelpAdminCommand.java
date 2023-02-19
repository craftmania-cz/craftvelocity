package cz.craftmania.craftvelocity.commands.admin;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.commands.CraftCommand;
import cz.craftmania.craftvelocity.objects.GHelpData;
import cz.craftmania.craftvelocity.utils.ChatInfo;
import cz.craftmania.craftvelocity.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.List;
import java.util.UUID;

public class GhelpAdminCommand implements CraftCommand {

    @Override
    public String getCommandAlias() {
        return "/ghelp";
    }

    @Override
    public String[] getPermissionNodes() {
        return new String[]{"craftvelocity.command.ghelp", "craftmania.at.helper"};
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource commandSource = invocation.source();
        String[] arguments = invocation.arguments();

        if (!(commandSource instanceof Player player)) {
            Logger.error("Tento příkaz je pro hráče.");
            return;
        }

        // //ghelp list
        if (arguments.length == 1) {
            switch (arguments[0]) {
                case "list" -> {
                    player.sendMessage(Component.text("§r"));
                    player.sendMessage(Component.text("§7§l§m--------§r§7[ §e§lSeznam posledních GHelp zprav §7]§m--------"));
                    player.sendMessage(Component.text("§r"));
                    player.sendMessage(Component.text("    §7§oNajetím na nick uvidíš, odkud byla zpráva poslána."));
                    player.sendMessage(Component.text("    §7§oPo najetí na zprávu uvidíš čas odeslání."));
                    player.sendMessage(Component.text("§r"));

                    List<GHelpData> gHelpDataList = Main.getInstance().getGhelpManager().getGHelpDataList();

                    gHelpDataList.forEach(gHelpData -> {
                        player.sendMessage(gHelpData.generateChatMessage());
                    });

                    player.sendMessage(Component.text("§r"));
                    player.sendMessage(Component.text("§7§l§m---------------§r§7[ §e§lKonec seznamu §7]§m----------------"));
                    player.sendMessage(Component.text("§r"));

                    return;
                }
            }
        }

        // //ghelp delete UUID
        if (arguments.length == 2) {
            switch (arguments[0]) {
                case "delete" -> {
                    String possibleMessageUUID = arguments[1];
                    UUID messageUUID;

                    try {
                        messageUUID = UUID.fromString(possibleMessageUUID);
                    } catch (Exception ignored) {
                        ChatInfo.error(player, "UUID zprávy §e" + possibleMessageUUID + "{c} není validní UUID!");
                        return;
                    }

                    boolean deleted = Main.getInstance().getGhelpManager().deleteMessageByUUID(messageUUID);

                    if (deleted) {
                        ChatInfo.success(player, "GHelp zpráva s UUID §e" + messageUUID + "{c} byla smazána!");
                    } else {
                        ChatInfo.error(player, "Není zde žádná zpráva s UUID §e" + messageUUID + "{c}!");
                    }

                    return;
                }
            }
        }

        // //ghelp respond UUID <message>
        if (arguments.length > 2) {
            switch (arguments[0]) {
                case "respond" -> {
                    String possibleMessageUUID = arguments[1];
                    UUID messageUUID;

                    try {
                        messageUUID = UUID.fromString(possibleMessageUUID);
                    } catch (Exception ignored) {
                        ChatInfo.error(player, "UUID zprávy §e" + possibleMessageUUID + "{c} není validní UUID!");
                        return;
                    }

                    GHelpData gHelpData = Main.getInstance().getGhelpManager().getGhelpDataByMessageUUID(messageUUID);

                    if (gHelpData == null) {
                        ChatInfo.error(player, "Není zde žádná zpráva s UUID §e" + messageUUID + "{c}!");
                        return;
                    }

                    if (!gHelpData.isPlayerOnline()) {
                        ChatInfo.error(player, "Hráč §e" + gHelpData.getPlayerUsername() + "{c}, který napsal zprávu s UUID §e" + messageUUID + "{c} již není online - nelze mu odpovědět.");
                        sendDeleteGHelpQuestion(player, gHelpData);
                        return;
                    }

                    String[] argumentsMessage = new String[arguments.length - 2];
                    System.arraycopy(arguments, 2, argumentsMessage, 0, arguments.length - 2);
                    String respondMessage = String.join(" ", argumentsMessage);

                    Player gHelpPlayer = gHelpData.getPlayer();
                    sendRespondToPlayer(gHelpPlayer, gHelpData, respondMessage);
                    sendDeleteGHelpQuestion(player, gHelpData);
                    return;
                }
            }
        }
    }

    private void sendRespondToPlayer(Player player, GHelpData gHelpData, String message) {
        player.sendMessage(Component.text(""));
        ChatInfo.warning(player, "Člen A-Teamu ti odpověděl na tvou GHelp zprávu!");
        ChatInfo.info(player, "Tvá zpráva: §e" + gHelpData.getMessage());
        ChatInfo.info(player, "Odpověď A-Team člena: §e" + message);
        player.sendMessage(Component.text(""));
    }

    private void sendDeleteGHelpQuestion(Player player, GHelpData gHelpData) {
        player.sendMessage(Component.text("§r"));

        Component deleteGHelpMessage = Component.text(ChatInfo.infoMessage("Chcete smazat tento GHelp?"))
                                                .append(Component.text(" §c[SMAZAT]")
                                                                 .hoverEvent(HoverEvent.showText(Component.text("§cTato akce je nevratná.")))
                                                                 .clickEvent(ClickEvent.runCommand("//ghelp delete " + gHelpData.getMessageUUID())));
        player.sendMessage(deleteGHelpMessage);

        player.sendMessage(Component.text("§r"));
    }
}
