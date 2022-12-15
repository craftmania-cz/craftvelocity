package cz.craftmania.craftvelocity.utils;

import com.velocitypowered.api.event.EventTask;

public class Utils {

    private static int taskId = 0;

    public static void runAsync(Runnable runnable) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        String callerClass = stackTraceElement.getClassName();
        String callerMethod = stackTraceElement.getMethodName();

        new Thread(() -> {
            final int currentTaskId = ++taskId;
            Thread.currentThread().setName("CRAFTVELOCITY-TASK-" + currentTaskId);
            Logger.debug("[ASYNC] Task " + currentTaskId + " started called by " + callerClass + "#" + callerMethod);
            long start = System.currentTimeMillis();

            runnable.run();

            Logger.debug("[ASYNC] Task " + currentTaskId + " finished in " + (System.currentTimeMillis() - start) + "ms");
        }).start();
    }
}
