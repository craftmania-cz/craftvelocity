package cz.craftmania.craftvelocity.utils;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;

public class ChatInfo {

    public static String success(String message) {
        return "§a> " + message.replace("{c}", "§a");
    }

    public static String info(String message) {
        return "§7> " + message.replace("{c}", "§7");
    }

    public static String warning(String message) {
        return "§6[!] " + message.replace("{c}", "§6");
    }

    public static String error(String message) {
        return "§c[!!] " + message.replace("{c}", "§c");
    }

    public static void success(CommandSource source, String message) {
        process(source, success(message));
    }

    public static void info(CommandSource source, String message) {
        process(source, info(message));
    }

    public static void warning(CommandSource source, String message) {
        process(source, warning(message));
    }

    public static void error(CommandSource source, String message) {
        process(source, error(message));
    }

    private static void process(CommandSource source, String message) {
        source.sendMessage(Component.text(message));
    }
}
