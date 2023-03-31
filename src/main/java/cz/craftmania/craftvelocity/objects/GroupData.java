package cz.craftmania.craftvelocity.objects;

import lombok.Getter;

import java.util.List;

/**
 * Holds information about config's tab completion group
 */
public class GroupData {

    private boolean whitelist;
    private List<String> commands;

    public GroupData(List<String> commands, boolean whitelist) {
        this.commands = commands;
        this.whitelist = whitelist;
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
    public boolean isWhitelist() {
        return this.whitelist;
    }

    @Override
    public String toString() {
        return "GroupData{" +
                "isWhitelist=" + whitelist +
                ", commands=" + commands +
                '}';
    }

}
