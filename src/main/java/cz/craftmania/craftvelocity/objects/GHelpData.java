package cz.craftmania.craftvelocity.objects;

import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.Main;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class GHelpData {

    private @Getter String playerUsername;
    private @Getter UUID playerUUID;
    private @Getter UUID messageUUID;
    private @Getter String playerServer;
    private @Getter String message;
    private @Getter long time;

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
}
