package cz.craftmania.craftvelocity.api.craftmania;

import cz.craftmania.craftvelocity.api.craftmania.objects.PlayerInfo;
import dev.mayuna.simpleapi.APIRequest;
import dev.mayuna.simpleapi.Action;
import dev.mayuna.simpleapi.PathParameter;
import dev.mayuna.simpleapi.SimpleAPI;
import lombok.NonNull;

/**
 * Wraps CraftMania's API into a {@link SimpleAPI}
 */
public class CraftManiaAPI extends SimpleAPI {

    private static CraftManiaAPI instance;

    /**
     * Gets singleton's instance
     * @return Non-null {@link CraftManiaAPI}
     */
    public static CraftManiaAPI getInstance() {
        if (instance == null) {
            instance = new CraftManiaAPI();
        }

        return instance;
    }

    @Override
    public @NonNull String getURL() {
        return "https.//api.craftmania.cz/";
    }

    /**
     * Fetches {@link PlayerInfo} from CraftMania's API
     * @param nick Non-null player's nick
     * @return {@link Action} with {@link PlayerInfo}
     */
    public Action<PlayerInfo> fetchPlayerInfo(@NonNull String nick) {
        return new Action<>(this, PlayerInfo.class, new APIRequest.Builder()
                .setEndpoint("/player/{nick}")
                .setMethod("GET")
                .addPathParameter(new PathParameter("nick", nick))
                .build()
        );
    }
}
