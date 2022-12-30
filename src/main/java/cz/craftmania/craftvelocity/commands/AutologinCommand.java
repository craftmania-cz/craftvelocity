package cz.craftmania.craftvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.data.PlayerIgnoredAutologinMessageData;
import cz.craftmania.craftvelocity.objects.AutologinPlayer;
import cz.craftmania.craftvelocity.utils.ChatInfo;
import cz.craftmania.craftvelocity.utils.Logger;
import dev.mayuna.pumpk1n.objects.DataHolder;
import net.kyori.adventure.text.Component;

import java.util.LinkedList;
import java.util.List;

public class AutologinCommand implements CraftCommand {

    @Override
    public String getCommandAlias() {
        return "autologin";
    }

    @Override
    public List<String> getSuggestion(Invocation invocation, String[] arguments, int argumentsCount) {
        List<String> suggestions = new LinkedList<>();

        switch (argumentsCount) {
            case 0, 1 -> {
                suggestions.add("on");
                suggestions.add("ignore");
            }
        }

        return suggestions;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource commandSource = invocation.source();
        String[] arguments = invocation.arguments();

        if (!(commandSource instanceof Player player)) {
            Logger.error("Tento příkaz je pro hráče. V konzoli použij //autologin");
            return;
        }

        if (arguments.length == 0) {
            AutologinPlayer autologinPlayer = Main.getInstance().getAutologinManager().getCache().getIfPresentFromAutologinPlayerCache(player.getUsername());

            if (autologinPlayer == null) {
                ChatInfo.info(commandSource, "Načítám tvé autologin data...");
            }

            Main.getInstance().getAutologinManager().fetchAutologinPlayer(player.getUsername()).whenCompleteAsync((autologinPlayerNew, throwable) -> {
                if (throwable != null) {
                    ChatInfo.error(commandSource, "Nastala chyba při získávání tvých autologin dat. Zkus to, prosím, později.");
                    return;
                }

                if (autologinPlayerNew == null) {
                    ChatInfo.info(commandSource, "Aktuálně máš §evypnutý{c} autologin.");
                } else {
                    ChatInfo.info(commandSource, "Aktuálně máš §azapnutý{c} autologin.");
                }
            });

            return;
        }

        if (arguments.length == 1) {
            String action = arguments[0];

            switch (action) {
                case "on" -> {
                    Main.getInstance().getAutologinManager().fetchAutologinPlayer(player.getUsername()).whenCompleteAsync(((autologinPlayer, throwable) -> {
                        if (throwable != null) {
                            ChatInfo.error(commandSource, "Nastala chyba při kontaktu databáze. Zkus to, prosím, později. Pokud tato chyba bude přetrvávat, prosím, kontaktuj nás. §8(§9/discord§8)");
                            return;
                        }

                        if (autologinPlayer != null) {
                            ChatInfo.error(commandSource, "Již máš zapnutý autologin!");
                            return;
                        }

                        ChatInfo.info(commandSource, "Zapínám ti autologin...");

                        Main.getInstance()
                            .getAutologinManager()
                            .enableAutologin(player.getUsername())
                            .whenCompleteAsync(((autologinPlayerNew, throwableEnabling) -> {
                                if (throwableEnabling != null) {
                                    ChatInfo.error(commandSource, "Nastala chyba při zapínání autologinu. Zkus to, prosím, později. Pokud tato chyba bude přetrvávat, prosím, kontaktuj nás. §8(§9/discord§8)");
                                    return;
                                }

                                if (autologinPlayerNew == null) {
                                    ChatInfo.error(commandSource, "Tvůj nick §e" + player.getUsername() + "{c} není originální! Nelze zapnout autologin pro warez hráče.");
                                    return;
                                }

                                player.disconnect(Component.text(Main.getInstance().getConfig().getAutologin().getMessages().getAutologinEnabled()));
                            }));
                    }));

                    return;
                }

                /*
                case "off" -> {
                    ChatInfo.info(commandSource, "Vypínám ti autologin...");

                    Main.getInstance().getAutologinManager().disableAutologin(player.getUsername()).whenCompleteAsync(((aVoid, throwable) -> {
                        if (throwable != null) {
                            ChatInfo.error(commandSource, "Nastala chyba při vypínání autologinu. Zkus to, prosím, později. Pokud tato chyba bude přetrvávat, prosím, kontaktuj nás. §8(§9/discord§8)");
                            return;
                        }

                        player.disconnect(Component.text(Main.getInstance().getConfig().getAutologin().getMessages().getAutologinDisabled()));
                    }));

                    return;
                }*/

                case "ignore" -> {
                    DataHolder playerDataHolder = Main.getInstance().getPumpk1n().getOrCreateDataHolder(player.getUniqueId());

                    if (playerDataHolder.getDataElement(PlayerIgnoredAutologinMessageData.class) == null) {
                        playerDataHolder.getOrCreateDataElement(PlayerIgnoredAutologinMessageData.class);
                        playerDataHolder.save();

                        ChatInfo.success(player, "Nyní se zpráva o autologinu již nezobrazí.");
                    } else {
                        playerDataHolder.removeDataElement(PlayerIgnoredAutologinMessageData.class);
                        playerDataHolder.save();

                        ChatInfo.success(player, "Zapnul sis zprávu o autologinu při připojení na server.");
                    }

                    return;
                }
            }

        }

        ChatInfo.error(commandSource, "Invalidní syntax příkazu. Syntax: /autologin [on|ignore]");
    }
}
