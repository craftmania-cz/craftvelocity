package cz.craftmania.craftvelocity.cache.objects;

import cz.craftmania.craftvelocity.api.minetools.objects.MineToolsPlayer;
import lombok.Getter;
import lombok.NonNull;

/**
 * Holds MineToolsPlayer object
 */
public class MineToolsCacheObject {

    private final @Getter MineToolsPlayer mineToolsPlayer;

    public MineToolsCacheObject(@NonNull MineToolsPlayer mineToolsPlayer) {
        this.mineToolsPlayer = mineToolsPlayer;
    }

    /**
     * See {@link MineToolsPlayer#isOriginalNick()}
     */
    public boolean isOriginalNick() {
        return mineToolsPlayer.isOriginalNick();
    }

    @Override
    public String toString() {
        return "MineToolsCacheObject{" +
                "mineToolsPlayer=" + mineToolsPlayer +
                '}';
    }
}
