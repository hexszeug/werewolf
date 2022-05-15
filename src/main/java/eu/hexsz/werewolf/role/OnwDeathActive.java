package eu.hexsz.werewolf.role;

import eu.hexsz.werewolf.controller.ExecutionService;
import eu.hexsz.werewolf.controller.Job;

/**
 * Is implemented by roles that have a special ability that is triggered by the death of the role.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public interface OnwDeathActive extends PlayerController {

    /**
     * Is called if the corresponding {@link eu.hexsz.werewolf.player.Player} should die.
     * Should kill the player unless the role somehow allows to survive a kill.
     * @param job The job to close when the player died or finished surviving.
     * @since 1.0-SNAPSHOT
     * */
    void die(Job job, ExecutionService.Execution execution);
}
