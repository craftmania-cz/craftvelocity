package cz.craftmania.craftvelocity.api.minetools;

import cz.craftmania.craftvelocity.api.minetools.objects.MineToolsPlayer;
import dev.mayuna.simpleapi.APIRequest;
import dev.mayuna.simpleapi.Action;
import dev.mayuna.simpleapi.PathParameter;
import dev.mayuna.simpleapi.SimpleAPI;
import lombok.NonNull;

import java.util.UUID;

/**
 * Wraps MineTools' API into a {@link SimpleAPI}
 */
public class MineToolsAPI extends SimpleAPI {

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
    public @NonNull String getURL() {
        return "https://api.minetools.eu";
    }

    /**
     * Fetches {@link MineToolsPlayer} from MineTools' API
     * @param nick Non-null player's nick
     * @return {@link Action} with {@link MineToolsPlayer}
     */
    public Action<MineToolsPlayer> getMineToolsPlayer(String nick) {
        return new Action<>(this, MineToolsPlayer.class, new APIRequest.Builder()
                .setEndpoint("/uuid/{nick}")
                .setMethod("GET")
                .addPathParameter(new PathParameter("nick", nick))
                .build());
    }

    /**
     * Fetches {@link MineToolsPlayer} from MineTools' API
     * @param uuid Non-null player's UUID
     * @return {@link Action} with {@link MineToolsPlayer}
     */
    public Action<MineToolsPlayer> getMineToolsPlayer(UUID uuid) {
        return new Action<>(this, MineToolsPlayer.class, new APIRequest.Builder()
                .setEndpoint("/uuid/{uuid}")
                .setMethod("GET")
                .addPathParameter(new PathParameter("uuid", uuid.toString()))
                .build());
    }
}
