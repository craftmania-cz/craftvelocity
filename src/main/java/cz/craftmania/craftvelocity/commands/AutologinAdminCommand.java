package cz.craftmania.craftvelocity.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.velocitypowered.api.command.CommandSource;

@Subcommand("/autologin")
@CommandPermission("craftvelocity.admin.autologin")
public class AutologinAdminCommand extends BaseCommand {

    @Default
    public void showAutologinStatistics(CommandSource source) {
        // TODO: Write out cache info
    }

    @Subcommand("status")
    public void changeAutologinForPlayer(CommandSource source, String playerNick, boolean enabled) {
        
    }
}
