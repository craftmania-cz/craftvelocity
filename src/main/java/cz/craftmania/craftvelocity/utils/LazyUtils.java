package cz.craftmania.craftvelocity.utils;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

public class LazyUtils {

    public static void sendMessageToPlayer(Player player, String message) {
        player.sendMessage(Component.text(message));
    }

}
