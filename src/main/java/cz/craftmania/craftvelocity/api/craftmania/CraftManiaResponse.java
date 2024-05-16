package cz.craftmania.craftvelocity.api.craftmania;

import dev.mayuna.simpleapi.GsonApiResponse;
import lombok.Getter;

public abstract class CraftManiaResponse extends GsonApiResponse<CraftManiaAPI> {

    private @Getter String status;
    private @Getter boolean success;
    private @Getter String error;
}
