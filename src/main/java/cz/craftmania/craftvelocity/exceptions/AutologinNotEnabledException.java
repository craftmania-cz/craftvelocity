package cz.craftmania.craftvelocity.exceptions;

import lombok.Getter;

public class AutologinNotEnabledException extends RuntimeException {

    private final @Getter String nick;

    public AutologinNotEnabledException(String nick) {
        super("Hráč s nickem " + nick + " nemá zapnutý autologin!");
        this.nick = nick;
    }
}
