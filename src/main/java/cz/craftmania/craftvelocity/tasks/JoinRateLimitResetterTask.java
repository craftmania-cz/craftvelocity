package cz.craftmania.craftvelocity.tasks;

import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.listeners.JoinRateLimitListener;

public class JoinRateLimitResetterTask implements CraftTaskTimer {

    @Override
    public String getName() {
        return "JoinRateLimitResetterTask";
    }

    @Override
    public long getDelay() {
        return Main.getInstance().getConfig().getJoinRateLimit().getDelayMillis();
    }

    @Override
    public long getInterval() {
        return Main.getInstance().getConfig().getJoinRateLimit().getIntervalMillis();
    }

    @Override
    public boolean isFixed() {
        return true;
    }

    @Override
    public boolean logStartAndFinish() {
        return false;
    }

    @Override
    public void run() {
        JoinRateLimitListener.setCurrentConnectionCount(0);
    }
}
