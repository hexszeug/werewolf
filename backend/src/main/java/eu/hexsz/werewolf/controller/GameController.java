package eu.hexsz.werewolf.controller;

import eu.hexsz.werewolf.time.Time;
import lombok.RequiredArgsConstructor;

/**
 * The {@code GameController} is the initial instance
 * that starts and controls the game logic on a high level.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
@RequiredArgsConstructor
public class GameController {
    private Job job;

    //dependencies
    private final Time time;
    private final NightController nightController;
    private final DayController dayController;


    /**
     * Is called by {@link GameFactory} after all necessary objects
     * for the game are created to start the game logic.
     * @param job The job to close when the game ends.
     * @since 1.0-SNAPSHOT
     * */
    public void startGame(Job job) {
        if (job == null) {
            return;
        }
        this.job = job;
        startNight();
    }

    /**
     * Is called by the winning detection if the game ended.
     * @since 1.0-SNAPSHOT
     * */
    public void endGame() {
        job.done();
    }

    private void startNight() {
        new Job("night:" + time.getNight(), nightController::manageNight, this::startDay).start();
    }

    private void startDay() {
        new Job("day:" + time.getNight(), dayController::manageDay, this::startNight).start();
    }
}
