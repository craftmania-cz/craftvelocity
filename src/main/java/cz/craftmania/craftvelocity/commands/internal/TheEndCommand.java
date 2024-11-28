package cz.craftmania.craftvelocity.commands.internal;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.commands.CraftCommand;
import cz.craftmania.craftvelocity.utils.ChatInfo;
import cz.craftmania.craftvelocity.utils.Utils;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class TheEndCommand implements CraftCommand {


    @Override
    public String getCommandAlias() {
        return "the-end";
    }

    @Override
    public String[] getPermissionNodes() {
        return new String[]{"craftvelocity.command.the-end"};
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if ((source instanceof Player player)) {
            ChatInfo.error(source, "Tento příkaz je jen pro console!");
            return;
        }

        String text = "§e§lThe End\n\n§7Byla to dlouhá cesta, cesta na kterou se nedá zapomenout.\n§7A stejně tak jsi nezapomněl(a) ani ty na tento server.\n§7Děkujeme za všechny ty krásné chvíle, které jsme spolu prožili.\n\n§b§lCraftMania.cz\n§82014 - 2024";

        // Get all players
        Collection<Player> players = Main.getInstance().getServer().getAllPlayers();
        CompletableFuture.runAsync(() -> {
            players.forEach(player -> {
                Utils.kickPlayer(player.getUsername(), text);
            });
        });
    }
}
