package cz.craftmania.craftvelocity.api.minetools;

import com.google.gson.Gson;
import cz.craftmania.craftvelocity.api.minetools.objects.MineToolsCache;
import dev.mayuna.simpleapi.GsonApiResponse;
import lombok.Getter;
import lombok.NonNull;

public abstract class MineToolsResponse extends GsonApiResponse<MineToolsAPI> {

    private @Getter MineToolsCache cache;

    private @Getter String error;
    private @Getter String errorMessage;
    private @Getter String status;

    public boolean isError() {
        return status.equals("ERR");
    }

    @Override
    public @NonNull Gson getGson() {
        return new Gson();
    }
}
