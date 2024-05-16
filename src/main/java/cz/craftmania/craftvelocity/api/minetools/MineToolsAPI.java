package cz.craftmania.craftvelocity.api.minetools;

import cz.craftmania.craftvelocity.api.ManagedWrappedApi;
import cz.craftmania.craftvelocity.api.minetools.objects.MineToolsPlayer;
import dev.mayuna.simpleapi.ApiRequest;
import dev.mayuna.simpleapi.PathParameter;
import dev.mayuna.simpleapi.RequestMethod;
import dev.mayuna.simpleapi.WrappedApi;
import lombok.NonNull;

import java.util.UUID;

/**
 * Wraps MineTools' API into a {@link ManagedWrappedApi}
 */
public class MineToolsAPI extends ManagedWrappedApi {

    private static MineToolsAPI instance;

    public static UUID parseUndashedId(String id) {
        if (id == null) {
            return null;
        }

        return UUID.fromString(id.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }

    /**
     * Gets singleton's instance
     * @return Non-null {@link MineToolsAPI}
     */
    public static MineToolsAPI getInstance() {
        if (instance == null) {
            instance = new MineToolsAPI();
        }

        return instance;
    }

    @Override
    public @NonNull String getDefaultUrl() {
        return "https://api.minetools.eu";
    }

    /**
     * Fetches {@link MineToolsPlayer} from MineTools' API
     * @param nick Non-null player's nick
     * @return {@link ApiRequest} with {@link MineToolsPlayer}
     */
    public ApiRequest<MineToolsPlayer> getMineToolsPlayer(String nick) {
        return ApiRequest.builder(this, MineToolsPlayer.class)
                .withEndpoint("/uuid/{nick}")
                .withRequestMethod(RequestMethod.GET)
                .withPathParameter(PathParameter.of("nick", nick))
                .build();
    }

    /**
     * Fetches {@link MineToolsPlayer} from MineTools' API
     * @param uuid Non-null player's UUID
     * @return {@link ApiRequest} with {@link MineToolsPlayer}
     */
    public ApiRequest<MineToolsPlayer> getMineToolsPlayer(UUID uuid) {
        return ApiRequest.builder(this, MineToolsPlayer.class)
                .withEndpoint("/uuid/{uuid}")
                .withRequestMethod(RequestMethod.GET)
                .withPathParameter(PathParameter.of("uuid", uuid.toString()))
                .build();
    }
}
