package cz.craftmania.craftvelocity.api.minetools.objects;

import cz.craftmania.craftvelocity.api.minetools.MineToolsAPI;
import cz.craftmania.craftvelocity.api.minetools.MineToolsResponse;
import lombok.Getter;

import java.util.UUID;

/**
 * Holds information about player as UUID (id) and nick (name)
 */
public class MineToolsPlayer extends MineToolsResponse {

    private @Getter String id;
    private @Getter String name;

    /**
     * Checks if ID and name is null and status is OK. If everything passes, nick is recognized as original/premium nick and is returned true
     * @return True = original, otherwise warez.
     */
    public boolean isOriginalNick() {
        return id != null && name != null && getStatus().equals("OK");
    }

    /**
     * Parses MineTools' player id as Java UUID
     * @return Player UUID
     */
    public UUID getUUID() {
        return MineToolsAPI.parseUndashedId(id);
    }

    /**
     * Checks if supplied nick equals nick in instance
     * @param nick Player's nick
     * @return True if equals, otherwise false
     */
    public boolean isNickSame(String nick) {
        return name.equals(nick);
    }

    @Override
    public String toString() {
        return "MineToolsPlayer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
