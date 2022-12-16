package cz.craftmania.craftvelocity.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.velocitypowered.api.command.CommandSource;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.utils.ChatInfo;

@Subcommand("/autologin")
@CommandPermission("craftvelocity.admin.autologin")
public class AutologinAdminCommand extends BaseCommand {

    @Default
    public void showAutologinStatistics(CommandSource source) {
        // TODO: Write out cache info
    }

    @Default
    public void changeAutologinForPlayer(CommandSource source, Action action, String playerNick) {
        switch(action) {
            case ADD -> {

            }
            case REMOVE -> {

            }
            case CHECK -> {
                ChatInfo.info(source, "Načítám autologin data o hráči §e" + playerNick + "{c}...");

                Main.getInstance().getAutologinManager().fetchAutologinPlayer(playerNick).whenCompleteAsync((autologinPlayer, throwable) -> {
                    if (throwable != null) {
                        ChatInfo.error(source, "Nastala chyba při získávání autologin dat hráči §e" + playerNick + "{c}.");
                        return;
                    }

                    if (autologinPlayer == null) {
                        ChatInfo.info(source, "Hráč §e" + playerNick + "{c} má §evypnutý{c} autologin.");
                    } else {
                        ChatInfo.info(source, "Hráč §e" + playerNick + "{c} §azapnutý{c} autologin.");
                    }
                });

            }
        }
    }

    public enum Action {
        ADD,
        REMOVE,
        CHECK
    }
}
