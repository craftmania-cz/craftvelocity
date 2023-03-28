package cz.craftmania.craftvelocity.commands.autologin;

import com.velocitypowered.api.command.CommandSource;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.cache.AutologinCache;
import cz.craftmania.craftvelocity.commands.CraftCommand;
import cz.craftmania.craftvelocity.utils.ChatInfo;
import cz.craftmania.craftvelocity.utils.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class AutologinAdminCommand implements CraftCommand {

    @Override
    public String getCommandAlias() {
        return "/autologin";
    }

    @Override
    public String[] getPermissionNodes() {
        return new String[]{"craftvelocity.autologin.admin"};
    }

    @Override
    public List<String> getSuggestion(Invocation invocation, String[] arguments, int argumentsCount) {
        List<String> suggestions = new LinkedList<>();

        switch (argumentsCount) {
            case 0, 1: {
                suggestions.add("enable");
                suggestions.add("disable");
                suggestions.add("check");
                suggestions.add("clear-cache");

                if (argumentsCount == 1) {
                    if (!suggestions.contains(arguments[0])) {
                        break;
                    } else {
                        suggestions.clear();
                    }
                }
            }
            case 2: {
                Main.getInstance().getServer().getAllPlayers().forEach(player -> {
                    suggestions.add(player.getUsername());
                });
            }
        }

        return suggestions;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] arguments = invocation.arguments();

        if (arguments.length == 0) {
            AutologinCache cache = Main.getInstance().getAutologinManager().getCache();

            ChatInfo.info(source, "== Autologin Cache Stats ==");
            ChatInfo.info(source, "> MineToolsCache size: " + cache.getResolvedMineToolsPlayersCache().size());
            ChatInfo.info(source, "> AutologinPlayerCache size: " + cache.getResolvedAutologinPlayerCache().size());
            ChatInfo.info(source, "> DisabledAutologinPlayerCache size: " + cache.getDisabledAutologinPlayerCache().size());
            return;
        }

        if (arguments.length == 1) {
            String action = arguments[0].toLowerCase();

            AutologinCache cache = Main.getInstance().getAutologinManager().getCache();

            switch (action) {
                case "verbose" -> {
                    ChatInfo.info(source, "== Autologin Cache Verbose Stats ==");

                    var mineToolsCache = cache.getResolvedMineToolsPlayersCache();
                    ChatInfo.info(source, "= MineToolsCache = Size: " + mineToolsCache.size());
                    mineToolsCache.asMap().forEach((key, value) -> {
                        ChatInfo.info(source, "- " + key + " > " + value.toString());
                    });

                    var autologinPlayerCache = cache.getResolvedAutologinPlayerCache();
                    ChatInfo.info(source, "= AutologinPlayerCache = Size: " + autologinPlayerCache.size());
                    autologinPlayerCache.asMap().forEach((key, value) -> {
                        ChatInfo.info(source, "- " + key + " > " + value.toString());
                    });

                    var disabledAutologinPlayerCache = cache.getDisabledAutologinPlayerCache();
                    ChatInfo.info(source, "= DisabledAutologinPlayerCache = Size: " + disabledAutologinPlayerCache.size());
                    disabledAutologinPlayerCache.asMap().forEach((key, value) -> {
                        ChatInfo.info(source, "- " + key + " > " + value.toString());
                    });

                    return;
                }
                case "clear-cache" -> {
                    ChatInfo.info(source, "Mažu Autologin Cache...");

                    cache.invalidateAll();

                    ChatInfo.success(source, "Vymazal jsi Autologin Cache.");
                    return;
                }
            }
        }

        if (arguments.length == 2) {
            String action = arguments[0].toUpperCase();
            String playerNick = arguments[1];

            switch (action) {
                case "ENABLE" -> {
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

                        Utils.kickPlayer(playerNick, Main.getInstance().getConfig().getAutologin().getMessages().getAutologinEnabledForced());

                        ChatInfo.success(source, "Úspěšně jste zapnuli autologin pro nick §e" + playerNick + "{c}! Pokud hráč byl online, byl vykopnut.");
                    });

                    return;
                }
                case "DISABLE" -> {
                    ChatInfo.info(source, "Vypínám autologin pro hráče §e" + playerNick + "{c}...");

                    Main.getInstance().getAutologinManager().disableAutologin(playerNick).whenCompleteAsync((aVoid, throwable) -> {
                        if (throwable != null) {
                            ChatInfo.error(source, "Nastala chyba při vypínání autologinu pro hráče §e" + playerNick + "{c}.");
                            return;
                        }

                        Utils.kickPlayer(playerNick, Main.getInstance().getConfig().getAutologin().getMessages().getAutologinDisabledForced());

                        ChatInfo.success(source, "Úspěšně jste vypnuli autologin pro nick §e" + playerNick + "{c}! Pokud hráč byl online, byl vykopnut.");
                    });

                    return;
                }
                case "CHECK" -> {
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

                    return;
                }
            }
        }

        if (arguments.length == 3) {
            String action = arguments[0].toUpperCase();
            String playerNick = arguments[1];
            UUID playerUUID;

            try {
                playerUUID = UUID.fromString(arguments[2]);
            } catch (Exception exception) {
                ChatInfo.error(source, "Natala chyba při parsování UUID " + arguments[2]);
                return;
            }

            switch (action) {
                case "FORCE-ENABLE" -> {
                    ChatInfo.info(source, "Manuálně zapínám autologin pro hráče §e" + playerNick + "{c} s UUID §e" + playerUUID + "{c}...");

                    Main.getInstance().getAutologinManager().enableAutologin(playerNick, playerUUID).whenCompleteAsync(((autologinPlayer, throwable) -> {
                        if (throwable != null) {
                            ChatInfo.error(source, "Nastala chyba při zapínání autologinu pro hráče §e" + playerNick + "{c} s UUID §e" + playerUUID + "!");
                            return;
                        }

                        Utils.kickPlayer(playerNick, Main.getInstance().getConfig().getAutologin().getMessages().getAutologinDisabledForced());

                        ChatInfo.success(source, "Úspěšně jste zapnuli autologin pro nick §e" + playerNick + " s UUID §e" + playerUUID + "{c}! Pokud hráč byl online, byl vykopnut.");
                    }));

                    return;
                }
            }
        }

        ChatInfo.error(source, "Invalidní syntax příkazu. Syntax: //autologin [[enable|disable|check <nick>]|[force-enable <nick> <uuid>]|clear-cache]");
    }
}
