package eu.hexsz.werewolf.role;

import eu.hexsz.werewolf.controller.ExecutionService;

/**
 * Is implemented by roles that want to be notified if someone dies.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public interface DeathListener extends PlayerController {

    /**
     * Is called every time a player dies by the
     * {@link eu.hexsz.werewolf.controller.ExecutionService ExecutionService}.
     * @param execution The execution containing the {@link eu.hexsz.werewolf.player.Player Player}
     *                  and the {@link eu.hexsz.werewolf.controller.ExecutionService.ExecutionQueue ExecutionQueue}
     *                  of the execution to add other players to the execution.
     * @since 1.0-SNAPSHOT
     * */
    void onDeath(ExecutionService.Execution execution);
}
