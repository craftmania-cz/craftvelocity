package cz.craftmania.craftvelocity.objects;

import java.util.List;
import java.util.Set;

/**
 * Holds information about config's tab completion group
 */
public class GroupData {

    private boolean isWhitelist;
    private List<String> commands;

    public GroupData(List<String> commands, boolean isWhitelist) {
        this.commands = commands;
        this.isWhitelist = isWhitelist;
    }

    /**
     * Checks if the group has command in command list
     * @param command Command
     * @return True if command exists in command list, otherwise false
     */
    public boolean has(String command) {
        return this.commands.contains(command);
    }

    /**
     * Checks if group is set to whitelist commands
     * @return True if is set to whitelist commands
     */
    boolean isWhitelist() {
        return this.isWhitelist;
    }

    @Override
    public String toString() {
        return "GroupData{" +
                "isWhitelist=" + isWhitelist +
                ", commands=" + commands +
                '}';
    }

}
