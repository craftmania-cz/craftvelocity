package cz.craftmania.craftvelocity.api.craftmania;

import cz.craftmania.craftvelocity.api.ManagedWrappedApi;
import cz.craftmania.craftvelocity.api.craftmania.objects.PlayerInfo;
import dev.mayuna.simpleapi.ApiRequest;
import dev.mayuna.simpleapi.PathParameter;
import dev.mayuna.simpleapi.RequestMethod;
import dev.mayuna.simpleapi.WrappedApi;
import lombok.NonNull;

/**
 * Wraps CraftMania's API into a {@link ManagedWrappedApi}
 */
public class CraftManiaAPI extends ManagedWrappedApi {

    private static CraftManiaAPI instance;

    /**
     * Gets singleton's instance
     *
     * @return Non-null {@link CraftManiaAPI}
     */
    public static CraftManiaAPI getInstance() {
        if (instance == null) {
            instance = new CraftManiaAPI();
        }

        return instance;
    }

    @Override
    public String getDefaultUrl() {
        return "https.//api.craftmania.cz/";
    }

    /**
     * Fetches {@link PlayerInfo} from CraftMania's API
     *
     * @param nick Non-null player's nick
     *
     * @return {@link ApiRequest} with {@link PlayerInfo}
     */
    public ApiRequest<PlayerInfo> fetchPlayerInfo(@NonNull String nick) {
        return ApiRequest.builder(this, PlayerInfo.class)
                         .withEndpoint("/player/{nick}")
                         .withRequestMethod(RequestMethod.GET)
                         .withPathParameter(PathParameter.of("nick", nick))
                         .build();
    }
}
