package cz.craftmania.craftvelocity.api.minetools.objects;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Holds hit, cacheTime in millis, cacheTimeLeft in millis, cacheAt in millis as UTC time and cacheUntil in millis as UTC time
 */
public class MineToolsCache {

    private @Getter @SerializedName("HIT") boolean hit;
    private @Getter @SerializedName("cache_time") long cacheTime;
    private @Getter @SerializedName("cache_time_left") long cacheTimeLeft;
    private @Getter @SerializedName("cached_at") double cacheAt;
    private @Getter @SerializedName("cache_until") double cacheUntil;
}
