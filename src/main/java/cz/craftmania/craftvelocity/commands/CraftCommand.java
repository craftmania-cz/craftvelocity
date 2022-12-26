package cz.craftmania.craftvelocity.commands;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.utils.Utils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CraftCommand extends SimpleCommand {

    String getCommandAlias();

    default String[] getCommandAliases() {
        return new String[0];
    }

    default String[] getPermissionNodes() {
        return null;
    }

    default void registerCommand(CommandManager commandManager) {
        commandManager.register(getCommandAlias(), this, getCommandAliases());
    }

    List<String> getSuggestion(Invocation invocation, String[] arguments, int argumentsCount);

    @Override
    default List<String> suggest(Invocation invocation) {
        return suggestAsync(invocation).join();
    }

    @Override
    default CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        CompletableFuture<List<String>> completableFuture = new CompletableFuture<>();

        Utils.runUnmaintainedAsync(() -> {
            completableFuture.complete(getSuggestion(invocation, invocation.arguments(), invocation.arguments().length));
        });

        return completableFuture;
    }

    @Override
    default boolean hasPermission(Invocation invocation) {
        String[] permissionNodes = getPermissionNodes();

        if (permissionNodes == null) {
            return true;
        }

        CommandSource source = invocation.source();

        if (!(source instanceof Player player)) {
            return true; // Konzole
        }

        for (String permissionNode : permissionNodes) {
            if (player.hasPermission(permissionNode)) {
                return true;
            }
        }

        return false;
    }
}
