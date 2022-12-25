package cz.craftmania.craftvelocity.cache.objects;

import cz.craftmania.craftvelocity.api.minetools.objects.MineToolsPlayer;
import lombok.Getter;
import lombok.NonNull;

public class MineToolsCacheObject {

    private final @Getter MineToolsPlayer mineToolsPlayer;

    public MineToolsCacheObject(@NonNull MineToolsPlayer mineToolsPlayer) {
        this.mineToolsPlayer = mineToolsPlayer;
    }

    public boolean isOriginalNick() {
        return mineToolsPlayer.isOriginalNick();
    }
}
