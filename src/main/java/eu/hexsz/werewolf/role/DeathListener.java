package eu.hexsz.werewolf.role;

import eu.hexsz.werewolf.player.Player;

/**
 * Is implemented by roles that want to be notified if someone dies.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public interface DeathListener extends PlayerController {

    /**
     * Is called every time a player dies by the {@link eu.hexsz.werewolf.controller.ExecutionService}.
     * @param dead The player who died.
     * @since 1.0-SNAPSHOT
     * */
    void onDeath(Player dead);
}
