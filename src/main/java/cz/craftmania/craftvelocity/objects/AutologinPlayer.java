package cz.craftmania.craftvelocity.objects;

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
        nick = newNick;
        // TODO: Update v SQL
    }

    public void updateLastOnline() {
        lastOnline = new Date();
        // TODO: Update v SQL
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
