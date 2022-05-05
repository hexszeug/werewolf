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
     * Is called by {@link GameSetupService} after all necessary objects
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
        nightController.manageNight(new Job("night:" + time.getNight(), this::startDay));
    }

    private void startDay() {
        dayController.manageDay(new Job("day:" + time.getNight(), this::startNight));
    }
}
