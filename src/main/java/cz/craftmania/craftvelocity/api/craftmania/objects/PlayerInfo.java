package cz.craftmania.craftvelocity.api.craftmania.objects;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Holds {@link PlayerInfoData}
 */
public class PlayerInfo {

    private @Getter PlayerInfoData data;

    /**
     * Holds player's nick, uuid and played time in minutes
     */
    public class PlayerInfoData {

        private @Getter String nick;
        private @Getter String uuid;
        private @Getter @SerializedName("played_time") long playedTime;
    }
}
