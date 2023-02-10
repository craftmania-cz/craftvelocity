package cz.craftmania.craftvelocity.tasks;

import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.Main;

public class PlayerUpdateTask implements CraftTaskTimer {

    @Override
    public String getName() {
        return "PlayerUpdateTask";
    }

    @Override
    public long getDelay() {
        return Main.getInstance().getConfig().getPlayerUpdateTask().getDelayMillis();
    }

    @Override
    public long getInterval() {
        return Main.getInstance().getConfig().getPlayerUpdateTask().getIntervalMillis();
    }

    @Override
    public boolean isFixed() {
        return true;
    }

    @Override
    public void run() {
        for (Player player : Main.getInstance().getServer().getAllPlayers()) {
            if (player.isActive()) {
                Main.getInstance().getSqlManager().updateTime(player);
            }
        }
    }
}
