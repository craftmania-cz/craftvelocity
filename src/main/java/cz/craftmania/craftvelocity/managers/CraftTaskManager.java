package cz.craftmania.craftvelocity.managers;

import cz.craftmania.craftvelocity.tasks.CraftTask;
import cz.craftmania.craftvelocity.tasks.CraftTaskTimer;
import cz.craftmania.craftvelocity.utils.Logger;
import lombok.Getter;

import java.util.Timer;
import java.util.TimerTask;

public class CraftTaskManager {

    private final @Getter Timer timer = new Timer();

    public void register(CraftTask craftTask) {
        if (craftTask instanceof CraftTaskTimer craftTaskTimer) {
            if (craftTaskTimer.isFixed()) {
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runTask(craftTask);
                    }
                }, craftTaskTimer.getDelay(), craftTaskTimer.getInterval());
            } else {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runTask(craftTask);
                    }
                }, craftTaskTimer.getDelay(), craftTaskTimer.getInterval());
            }

            return;
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runTask(craftTask);
            }
        }, craftTask.getDelay());
    }

    private void runTask(CraftTask craftTask) {
        if (craftTask.logStartAndFinish()) {
            Logger.debug("[TASK] Byl spuštěn task s názvem " + craftTask.getName() + " (timer task: " + (craftTask instanceof CraftTaskTimer) + ")");
        }
        long start = System.currentTimeMillis();
        boolean successful = true;

        try {
            craftTask.run();
        } catch (Exception exception) {
            Logger.error("[TASK] Při běhu tasku s názvem " + craftTask.getName() + " nastal exception!", exception);
            successful = false;
        }

        if (craftTask.logStartAndFinish()) {
            Logger.debug("[TASK] Task s názvem " + craftTask.getName() + " (timer task: " + (craftTask instanceof CraftTaskTimer) + ") byl dokončen (úspěšně: " + successful + ") v čase " + (System.currentTimeMillis() - start) + "ms");
        }
    }
}
