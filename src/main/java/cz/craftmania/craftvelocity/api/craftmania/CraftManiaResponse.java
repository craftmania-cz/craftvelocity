package cz.craftmania.craftvelocity.api.craftmania;

import dev.mayuna.simpleapi.APIResponse;
import dev.mayuna.simpleapi.deserializers.GsonDeserializer;
import lombok.Getter;

public abstract class CraftManiaResponse extends APIResponse<CraftManiaAPI> implements GsonDeserializer {

    private @Getter String status;
    private @Getter boolean success;
    private @Getter String error;
}
