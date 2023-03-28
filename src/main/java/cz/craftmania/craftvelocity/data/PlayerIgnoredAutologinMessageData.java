package cz.craftmania.craftvelocity.data;

import dev.mayuna.pumpk1n.api.ParentedDataElement;
import lombok.Getter;

/**
 * Determines if player has ignored Autologin message by mere existences in uuid's data holder
 */
public class PlayerIgnoredAutologinMessageData extends ParentedDataElement {

    private final @Getter boolean messageIgnored = true;

}
