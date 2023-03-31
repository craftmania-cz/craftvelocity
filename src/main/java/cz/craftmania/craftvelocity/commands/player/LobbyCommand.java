package cz.craftmania.craftvelocity.commands.player;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.commands.CraftCommand;
import cz.craftmania.craftvelocity.utils.ChatInfo;

public class LobbyCommand implements CraftCommand {

    @Override
    public String getCommandAlias() {
        return "lobby";
    }

    @Override
    public String[] getCommandAliases() {
        return new String[]{"hub"};
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if ((!(source instanceof Player player))) {
            ChatInfo.error(source, "Tento příkaz je jen pro hráče!");
            return;
        }

        if (player.getCurrentServer().get().getServerInfo().getName().equals(Main.getInstance().getConfig().getAutologin().getServers().getAuth())) {
            ChatInfo.error(player, "Nelze se teleportovat na lobby na přihlašovacím severu.");
            return;
        }

        if (player.getCurrentServer().get().getServerInfo().getName().contains("lobby")) {
            ChatInfo.error(player, "Již jsi na lobby!");
            return;
        }

        RegisteredServer lobbyServer = Main.getInstance().getServer().getServer("lobby2").orElse(null);
        if (lobbyServer == null) {
            ChatInfo.warning(source, "Nebylo možné tě připojit na lobby server!");
            return;
        }

        ChatInfo.success(source, "Přepojuji tě na lobby server...");
        player.createConnectionRequest(lobbyServer).fireAndForget();
    }
}
