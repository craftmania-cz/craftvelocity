package cz.craftmania.craftvelocity.tasks;

public interface CraftTaskTimer extends CraftTask {

    long getInterval();

    boolean isFixed();
}
