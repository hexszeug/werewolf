package eu.hexsz.werewolf.role;

import eu.hexsz.werewolf.controller.Job;
import eu.hexsz.werewolf.controller.NightController;
import eu.hexsz.werewolf.player.Player;

/**
 * Is implemented by roles that do anything in the night.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public interface NightActive extends PlayerController {

    /**
     * Is called by {@link NightController} to remind the {@code PlayerController}
     * to set all alarm for the next night.
     * @see NightController
     * @since 1.0-SNAPSHOT
     * */
    void setAlarms();

    /**
     * Is called by {@link NightController} when the {@code PlayerController} was chosen
     * to manage a night phase.
     * <br>The {@code NightController} awakes the {@link Player}s and tells them to fall asleep again,
     * so the {@code PlayerController} shouldn't do this.
     * @param job The job to close when the night phase should end.
     * @since 1.0-SNAPSHOT
     * */
    void manageNightPhase(Job job);
}
