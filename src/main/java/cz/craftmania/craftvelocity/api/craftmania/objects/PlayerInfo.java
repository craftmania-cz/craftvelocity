package cz.craftmania.craftvelocity.api.craftmania.objects;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Synchronized;

public class PlayerInfo {

    private @Getter PlayerInfoData data;

    public class PlayerInfoData {

        private @Getter String nick;
        private @Getter String uuid;
        private @Getter @SerializedName("played_time") long playedTime;
    }
}
