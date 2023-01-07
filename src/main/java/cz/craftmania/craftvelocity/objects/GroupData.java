package cz.craftmania.craftvelocity.objects;

import java.util.List;
import java.util.Set;

public class GroupData {

    private boolean isWhitelist;
    private List<String> commands;

    public GroupData(List<String> commands, boolean isWhitelist) {
        this.commands = commands;
        this.isWhitelist = isWhitelist;
    }

    public boolean has(String command) {
        return this.commands.contains(command);
    }

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
