package cz.craftmania.craftvelocity.objects;

import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.Main;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class GHelpData {

    private final @Getter String playerUsername;
    private final @Getter UUID playerUUID;
    private final @Getter UUID messageUUID;
    private final @Getter String playerServer;
    private final @Getter String message;
    private final @Getter long time;

    public GHelpData(String playerUsername, UUID playerUUID, UUID messageUUID, String playerServer, String message, long time) {
        this.playerUsername = playerUsername;
        this.playerUUID = playerUUID;
        this.messageUUID = messageUUID;
        this.playerServer = playerServer;
        this.message = message;
        this.time = time;
    }

    public Player getPlayer() {
        return Main.getInstance().getServer().getPlayer(playerUUID).orElse(null);
    }

    public boolean isPlayerOnline() {
        return getPlayer() != null;
    }

    public String formatTime() {
        return new SimpleDateFormat("HH:mm dd.MM.").format(new Date(time));
    }

    public Component generateChatMessage() {
        Component buttonRespondComponent = Component.text("§b[ODP]§r ")
                                                    .hoverEvent(HoverEvent.showText(Component.text("§7Kliknutím doplníš příkaz k odpovědi.")))
                                                    .clickEvent(ClickEvent.suggestCommand("//ghelp respond " + getMessageUUID() + " "));

        Component buttonTeleportComponent = Component.text("§b[TELE]§r ")
                                                     .hoverEvent(HoverEvent.showText(Component.text("§7Kliknutím se teleportuješ na hráčův server.")))
                                                     .clickEvent(ClickEvent.runCommand("/server " + getPlayerUsername()));

        Component buttonDeleteComponent = Component.text("§b[DEL]§r ")
                                                   .hoverEvent(HoverEvent.showText(Component.text("§7Kliknutím doplníš příkaz k smazání zprávy.")))
                                                   .clickEvent(ClickEvent.runCommand("//ghelp delete " + getMessageUUID()));

        Component playerNickComponent = Component.text("§e" + getPlayerUsername() + "§7: ")
                                                 .hoverEvent(HoverEvent.showText(Component.text("§7Odesláno: §e" + formatTime() + "\n" +
                                                                                                        "§7Server: §e" + getPlayerServer() + "\n" +
                                                                                                        "§7Hráč je: " + (isPlayerOnline() ? "§aOnline" : "§cOffline") + "\n" +
                                                                                                        "§8UUID zprávy: " + messageUUID)))
                                                 .clickEvent(ClickEvent.suggestCommand("//ghelp respond " + getMessageUUID() + " "));

        Component messageComponent = Component.text(getMessage());

        return Component.text("")
                        .append(buttonRespondComponent)
                        .append(buttonTeleportComponent)
                        .append(buttonDeleteComponent)
                        .append(playerNickComponent)
                        .append(messageComponent);
    }
}
