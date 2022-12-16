package cz.craftmania.craftvelocity.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.managers.AutologinManager;
import cz.craftmania.craftvelocity.utils.ChatInfo;
import cz.craftmania.craftvelocity.utils.Logger;

@Subcommand("autologin")
public class AutologinCommand extends BaseCommand {

    @Default
    public void showAutologinStatus(CommandSource commandSource) {
        if (!(commandSource instanceof Player player)) {
            Logger.error("Tento příkaz je pro hráče. V konzoli použij /autologin");
            return;
        }

        ChatInfo.info(commandSource, "Načítám tvé autologin data...");

        Main.getInstance().getAutologinManager().fetchAutologinPlayer(player.getUsername()).whenCompleteAsync((autologinPlayer, throwable) -> {
            if (throwable != null) {
                ChatInfo.error(commandSource, "Nastala chyba při získávání tvých autologin dat. Zkus to, prosím, později.");
                return;
            }

            if (autologinPlayer == null) {
                ChatInfo.info(commandSource, "Momentálně máš §evypnutý{c} autologin.");
            } else {
                ChatInfo.info(commandSource, "Momentálně máš §azapnutý{c} autologin.");
            }
        });
    }
}
