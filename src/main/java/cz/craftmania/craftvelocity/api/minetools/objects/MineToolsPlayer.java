package cz.craftmania.craftvelocity.api.minetools.objects;

import cz.craftmania.craftvelocity.api.minetools.MineToolsAPI;
import cz.craftmania.craftvelocity.api.minetools.MineToolsResponse;
import lombok.Getter;

import java.util.UUID;

public class MineToolsPlayer extends MineToolsResponse {

    private @Getter String id;
    private @Getter String name;

    public boolean isOriginalNick() {
        return id != null && name != null && getStatus().equals("OK");
    }

    public UUID getUUID() {
        return MineToolsAPI.parseUndashedId(id);
    }

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
