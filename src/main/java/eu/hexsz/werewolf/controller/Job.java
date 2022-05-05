package eu.hexsz.werewolf.controller;

import lombok.Getter;
import lombok.NonNull;

import java.util.logging.Logger;


/**
 * {@code Job} instances are given from higher controllers to lower ones to handle asynchronous job delegation.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public class Job {
    private final Logger logger = Logger.getAnonymousLogger();
    //TODO make multiple loggers for different rooms

    private final @NonNull @Getter String name;
    private final @NonNull Recall recall;

    /**
     * A {@code Job} should be given to a lower controller to start an asynchronous task.
     * The controller calls {@link Job#done()} when it completes the task
     * which notifies the creator of the {@code Job}.
     * @param name used for logging the start and end of a job.
     *             Should be lowercase with dashes (e.g. {@code "night-phase-werewolves"}).
     * @param recall method the job calls when {@link Job#done()} is run.
     * @since 1.0-SNAPSHOT
     * */
    public Job(String name, Recall recall) {
        this.name = name;
        this.recall = recall;
        logger.info(String.format("[%s] Started job", this.name));
    }

    /**
     * A method which is called when a job is completed.
     * @see Job
     * @since 1.0-SNAPSHOT
     * */
    public interface Recall {
        void execute();
    }

    /**
     * Should be called by the controller the job was given to.
     * Marks the job as completed and notifies the creator of the job.
     * @since 1.0-SNAPSHOT
     * */
    public void done() {
        logger.info(String.format("[%s] Finished job", name));
        recall.execute();
    }
}
