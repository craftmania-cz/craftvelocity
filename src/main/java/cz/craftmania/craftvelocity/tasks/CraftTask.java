package cz.craftmania.craftvelocity.tasks;

public interface CraftTask extends Runnable {

    String getName();

    long getDelay();

}
