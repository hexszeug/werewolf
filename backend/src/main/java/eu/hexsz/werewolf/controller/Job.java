package eu.hexsz.werewolf.controller;

import lombok.Getter;


/**
 * {@code Job} instances are given from higher controllers to lower ones to handle asynchronous job delegation.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public class Job {

    private final @Getter String name;
    private final Start start;
    private final Recall recall;

    private @Getter boolean running;
    private @Getter boolean done;

    /**
     * A {@code Job} should be given to a lower controller to start an asynchronous task.
     * The controller calls {@link Job#done()} when it completes the task
     * which notifies the creator of the {@code Job}.
     * @param name used for logging the start and end of a job.
     *             Should be lowercase with dashes (e.g. {@code "night-phase-werewolves"}).
     * @param recall method the job calls when {@link Job#done()} is run.
     * @since 1.0-SNAPSHOT
     * */
    public Job(String name, Start start, Recall recall) {
        this.name = name;
        this.start = start;
        this.recall = recall;
        running = false;
        done = false;
    }

    /**
     * Should be called by the creator of the job to start the job.
     * @since 1.0-SNAPSHOT
     * */
    public Job start() {
        if (running || done) {
            return this;
        }
        System.out.printf("[Job:%s] Started job%n", this.name);
        running = true;
        if (start != null) {
            start.execute(this);
        }
        return this;
    }

    /**
     * Should be called by the controller the job was given to.
     * Marks the job as completed and notifies the creator of the job.
     * @since 1.0-SNAPSHOT
     * */
    public void done() {
        if (!running || done) {
            return;
        }
        System.out.printf("[Job:%s] Finished job%n", name);
        running = false;
        done = true;
        if (recall != null) {
            recall.execute();
        }
    }

    /**
     * A method which is called when a job starts.
     * @see Job
     * @since 1.0-SNAPSHOT
     * @author hexszeug
     * */
    public interface Start {
        void execute(Job job);
    }

    /**
     * A method which is called when a job is completed.
     * @see Job
     * @since 1.0-SNAPSHOT
     * @author hexszeug
     * */
    public interface Recall {
        void execute();
    }
}
