package eu.hexsz.werewolf.controller;

import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.time.DayPhase;
import eu.hexsz.werewolf.time.Time;
import eu.hexsz.werewolf.update.AutoPlayerUpdateService;
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
    private final PlayerRegistry  playerRegistry;
    private final AutoPlayerUpdateService autoPlayerUpdateService;


    /**
     * Is called by {@link eu.hexsz.werewolf.GameFactory GameFactory} after all necessary objects
     * for the game are created to start the game logic.
     * @param job The job to close when the game ends.
     * @since 1.0-SNAPSHOT
     * */
    public void startGame(Job job) {
        if (job == null) {
            return;
        }

        this.job = job;

        for (Player player : playerRegistry) {
            autoPlayerUpdateService.onPlayerCreated(player);
        }

        time.nextPhase(); //from START_GAME to SUNSET

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
        if (time.getPhase() != DayPhase.SUNSET) {
            return;
        }
        time.nextPhase();
        new Job("night:" + time.getNight(), nightController::manageNight, this::startDay).start();
    }

    private void startDay() {
        if (time.getPhase() != DayPhase.SUNRISE) {
            return;
        }
        new Job("day:" + time.getNight(), dayController::manageDay, this::startNight).start();
    }
}
