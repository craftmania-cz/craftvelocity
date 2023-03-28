package cz.craftmania.craftvelocity.objects;

import cz.craftmania.craftvelocity.Main;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

/**
 * Holds information about Autologin Player such as player's UUID, nick and date of last appearance on the server
 */
public class AutologinPlayer {

    private final @Getter UUID uuid;
    private @Getter String nick;
    private @Getter Date lastOnline;

    public AutologinPlayer(UUID uuid, String nick) {
        this.uuid = uuid;
        this.nick = nick;
        this.lastOnline = new Date();
    }

    public AutologinPlayer(UUID uuid, String nick, Date lastOnline) {
        this.uuid = uuid;
        this.nick = nick;
        this.lastOnline = lastOnline;
    }

    /**
     * Updates nick in current instance if the current nick does not equal to the supplied nick
     * @param newNick Player's nick
     */
    public void updateNick(String newNick) {
        if (nick.equals(newNick)) {
            return;
        }

        nick = newNick;
    }

    /**
     * Updates last online time
     */
    public void updateLastOnline() {
        lastOnline = new Date();
    }

    /**
     * Updates player on SQL database (inserts or updates)
     */
    public void updateOnSQL() {
        Main.getInstance().getSqlManager().insertOrUpdateAutologinPlayer(this);
    }

    @Override
    public String toString() {
        return "AutologinPlayer{" +
                "uuid=" + uuid +
                ", nick='" + nick + '\'' +
                ", lastOnline=" + lastOnline +
                '}';
    }
}
