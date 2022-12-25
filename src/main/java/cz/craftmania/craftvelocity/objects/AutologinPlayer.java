package cz.craftmania.craftvelocity.objects;

import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.sql.SQLManager;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

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

    public void updateNick(String newNick) {
        if (nick.equals(newNick)) {
            return;
        }

        nick = newNick;
    }

    public void updateLastOnline() {
        lastOnline = new Date();
    }

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
