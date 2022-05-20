package eu.hexsz.werewolf.controller;

import lombok.Getter;

import java.util.TimerTask;

/**
 * Used for scheduling tasks once. Provides an easy way to cancel timer.
 * The timer can be restarted after the task was cancelled but not after the task is completed.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public class Timer {

    private final @Getter String name;
    private final @Getter long millis;
    private final Recall recall;

    private @Getter boolean running;
    private @Getter boolean done;

    private final java.util.Timer timer;
    private TimerTask timerTask;

    /**
     * Creates a new {@code Timer}.
     * @param name The name of the timer. Used for debugging.
     * @param millis The time between the start/restart of the timer
     *               and the end of the timer in milliseconds.
     * @param recall The recall to execute when the timer ends.
     * @since 1.0-SNAPSHOT
     * */
    public Timer(String name, long millis, Recall recall) {
        this.name = name;
        this.millis = millis;
        this.recall = recall;
        running = done = false;
        timer = new java.util.Timer(name);
    }

    /**
     * Starts / restarts the {@code Timer}.
     * Does nothing if the timer is currently running or the timer has already ended.
     * @since 1.0-SNAPSHOT
     * */
    public void start() {
        if (running || done) {
            return;
        }
        System.out.printf("[timer:%s:%s] Started timer%n", name, millis);
        running = true;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                done();
            }
        };
        timer.schedule(timerTask, millis);
    }

    /**
     * Cancels the {@code Timer}.
     * Does nothing if the timer isn't running.
     * <br>A canceled timer can be restarted by calling its {@link Timer#start() start()} method.
     * @since 1.0-SNAPSHOT
     * */
    public void cancel() {
        if (!running) {
            return;
        }
        System.out.printf("[timer:%s:%s] Canceled timer%n", name, millis);
        running = false;
        timerTask.cancel();
        timerTask = null;
    }

    private void done() {
        if (!running || done) {
            return;
        }
        System.out.printf("[timer:%s:%s] Finished timer%n", name, millis);
        running = false;
        done = true;
        timerTask.cancel();
        timerTask = null;
        timer.purge();
        recall.execute();
    }

    /**
     * A method which is called when a timer is completed.
     * @see Timer
     * @since 1.0-SNAPSHOT
     * */
    public interface Recall {
        void execute();
    }
}
