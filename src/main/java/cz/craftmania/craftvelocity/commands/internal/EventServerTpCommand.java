package cz.craftmania.craftvelocity.commands.internal;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.commands.CraftCommand;
import cz.craftmania.craftvelocity.utils.ChatInfo;

public class EventServerTpCommand implements CraftCommand {

    @Override
    public String getCommandAlias() {
        return "eventserver-tp";
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if ((!(source instanceof Player player))) {
            ChatInfo.error(source, "Tento příkaz je jen pro hráče!");
            return;
        }

        ServerConnection currentServer = player.getCurrentServer().orElse(null);

        if (currentServer == null) {
            ChatInfo.warning(source, "Nebylo možné tě připojit na Event Server! (nenacházíš se na žádném serveru?)");
            return;
        }

        if (Main.getInstance().getConfig().getAutologin().getServers().getAuth().contains(currentServer.getServerInfo().getName())) {
            ChatInfo.error(source, "Tento příkaz nemůžeš použít na Auth Serveru!");
            return;
        }

        RegisteredServer server = Main.getInstance().getServer().getServer("event-server").orElse(null);

        if (server == null) {
            ChatInfo.warning(source, "Nebylo možné tě připojit na Event Server! (event-server se nenachází na proxy? Nahlaš nám tuto chybu na Discord!)");
            return;
        }

        player.createConnectionRequest(server).fireAndForget();
    }
}
