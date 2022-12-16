package cz.craftmania.craftvelocity.utils;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;

public class ChatInfo {

    public static void info(CommandSource source, String message) {
        process(source, "§7", "> " + message);
    }

    public static void warning(CommandSource source, String message) {
        process(source, "§6", "[!] " + message);

    }

    public static void error(CommandSource source, String message) {
        process(source, "§e", "[!!] " + message);
    }

    private static void process(CommandSource source, String color, String message) {
        source.sendMessage(Component.text(color + message.replace("{c}", color)));
    }
}
