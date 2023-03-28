package cz.craftmania.craftvelocity.utils;

import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PluginMessageUtils {

    /**
     * Sends plugin vote message with player's nick, coin value and votetokens value
     * @param serverConnection Server connection
     * @param nick Player's nick
     * @param coins Coins
     * @param voteTokens VoteTokens
     */
    public static void sendVoteMessage(ServerConnection serverConnection, String nick, String coins, String voteTokens) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF("vote");
            out.writeUTF(nick);
            out.writeUTF(coins);
            out.writeUTF(voteTokens);

            // Backwards compatible with bungee
            serverConnection.sendPluginMessage(() -> "craftbungee", stream.toByteArray()); // 1.8-1.12 servery (FIXME: Useless?)
            serverConnection.sendPluginMessage(() -> "craftbungee:vote", stream.toByteArray()); // 1.13+ servery
        } catch (IOException exception) {
            Logger.error("Nastala chyba při zasílání Vote Plugin Message!", exception);
        }
    }
}
