package cz.craftmania.craftvelocity.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.velocitypowered.api.command.CommandSource;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.cache.AutologinCache;
import cz.craftmania.craftvelocity.utils.ChatInfo;

@Subcommand("/autologin")
@CommandPermission("craftvelocity.admin.autologin")
public class AutologinAdminCommand extends BaseCommand {

    @Default
    public void showAutologinStatistics(CommandSource source) {
        AutologinCache cache = Main.getInstance().getAutologinManager().getCache();

        ChatInfo.info(source, "== Autologin Cache Stats ==");
        ChatInfo.info(source, "> MineToolsCache size: " + cache.getResolvedMineToolsPlayersCache().size());
        ChatInfo.info(source, "> AutologinPlayerCache size: " + cache.getResolvedAutologinPlayerCache().size());
        ChatInfo.info(source, "> DisabledAutologinPlayerCache size: " + cache.getDisabledAutologinPlayerCache().size());
    }

    @Default
    public void changeAutologinForPlayer(CommandSource source, Action action, String playerNick) {
        switch(action) {
            case ENABLE -> {
                ChatInfo.info(source, "Zapínám autologin pro hráče §e" + playerNick + "{c}...");

                Main.getInstance().getAutologinManager().enableAutologin(playerNick).whenCompleteAsync((autologinPlayer, throwable) -> {
                    if (throwable != null) {
                        ChatInfo.error(source, "Nastala chyba při zapínání autologinu pro hráče §e" + playerNick + "{c}.");
                        return;
                    }

                    if (autologinPlayer == null) {
                        ChatInfo.error(source, "Nick §e" + playerNick + "{c} není originální. Nelze pro tento nick zapnout autologin!");
                        return;
                    }

                    ChatInfo.success(source, "Úspěšně jste zapnuli autologin pro nick §e" + playerNick + "{c}!");
                });
            }
            case DISABLE -> {
                ChatInfo.info(source, "Vypínám autologin pro hráče §e" + playerNick + "{c}...");

                Main.getInstance().getAutologinManager().disableAutologin(playerNick).whenCompleteAsync((aVoid, throwable) -> {
                    if (throwable != null) {
                        ChatInfo.error(source, "Nastala chyba při vypínání autologinu pro hráče §e" + playerNick + "{c}.");
                        return;
                    }

                    ChatInfo.success(source, "Úspěšně jste vypnuli autologin pro nick §e" + playerNick + "{c}!");
                });
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
        ENABLE,
        DISABLE,
        CHECK
    }
}
