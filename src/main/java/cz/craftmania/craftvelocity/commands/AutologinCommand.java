package cz.craftmania.craftvelocity.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.utils.ChatInfo;
import cz.craftmania.craftvelocity.utils.Logger;
import net.kyori.adventure.text.Component;

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
                ChatInfo.info(commandSource, "Aktuálně máš §evypnutý{c} autologin.");
            } else {
                ChatInfo.info(commandSource, "Aktuálně máš §azapnutý{c} autologin.");
            }
        });
    }

    @Subcommand("on")
    public void turnOnAutologin(CommandSource commandSource) {
        if (!(commandSource instanceof Player player)) {
            Logger.error("Tento příkaz je pro hráče. V konzoli použij /autologin");
            return;
        }

        ChatInfo.info(commandSource, "Zapínám ti autologin...");

        Main.getInstance().getAutologinManager().enableAutologin(player.getUsername()).whenCompleteAsync(((autologinPlayer, throwable) -> {
            if (throwable != null) {
                ChatInfo.error(commandSource, "Nastala chyba při zapínání autologinu. Zkus to, prosím, později. Pokud tato chyba bude přetrvávat, prosím, kontaktuj nás. §8(§9/discord§8)");
                return;
            }

            if (autologinPlayer == null) {
                ChatInfo.error(commandSource, "Tvůj nick §e" + player.getUsername() + "{c} není originální! Nelze zapnout autologin pro warez hráče.");
                return;
            }

            player.disconnect(Component.text(Main.getInstance().getConfig().getAutologin().getMessages().getAutologinEnabled()));
        }));
    }

    @Subcommand("off")
    public void turnOffAutologin(CommandSource commandSource) {
        if (!(commandSource instanceof Player player)) {
            Logger.error("Tento příkaz je pro hráče. V konzoli použij /autologin");
            return;
        }

        ChatInfo.info(commandSource, "Vypínám ti autologin...");

        Main.getInstance().getAutologinManager().disableAutologin(player.getUsername()).whenCompleteAsync(((aVoid, throwable) -> {
            if (throwable != null) {
                ChatInfo.error(commandSource, "Nastala chyba při zapínání autologinu. Zkus to, prosím, později. Pokud tato chyba bude přetrvávat, prosím, kontaktuj nás. §8(§9/discord§8)");
                return;
            }

            player.disconnect(Component.text(Main.getInstance().getConfig().getAutologin().getMessages().getAutologinEnabled()));
        }));
    }

    @Subcommand("ignore")
    public void ignoreAutologinMessage(CommandSource commandSource) {
        if (!(commandSource instanceof Player player)) {
            Logger.error("Tento příkaz je pro hráče. V konzoli použij /autologin");
            return;
        }
    }
}
