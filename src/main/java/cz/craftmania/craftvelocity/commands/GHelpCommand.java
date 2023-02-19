package cz.craftmania.craftvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.objects.GHelpData;
import cz.craftmania.craftvelocity.utils.ChatInfo;
import cz.craftmania.craftvelocity.utils.Logger;

public class GHelpCommand implements CraftCommand {

    @Override
    public String getCommandAlias() {
        return "ghelp";
    }

    @Override
    public String[] getCommandAliases() {
        return new String[]{"pomoc"};
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource commandSource = invocation.source();
        String[] arguments = invocation.arguments();

        if (!(commandSource instanceof Player player)) {
            Logger.error("Tento příkaz je pro hráče.");
            return;
        }

        if (arguments.length == 0) {
            ChatInfo.error(player, "Nenapsal jsi žádnou zprávu. Syntax: /ghelp <zpráva>");
            return;
        }

        if (Main.getInstance().getGhelpManager().isPlayerUnderCooldown(player.getUniqueId())) {
            ChatInfo.error(player, "Před zasláním další zprávy A-Teamu musíš počkat ještě nějakou chvíli.");
            return;
        }

        String message = String.join(" ", arguments);

        GHelpData ghelpData = Main.getInstance().getGhelpManager().addMessage(player, message);

        Main.getInstance().getGhelpManager().notifyAdminTeam(ghelpData);
        ChatInfo.info(player, "Tvá zpráva byla odeslána A-Teamu pod ID §e" + ghelpData.getMessageUUID() + "{c}! Vyčkej na jejich odpověď.");
    }
}
