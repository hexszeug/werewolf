package eu.hexsz.werewolf.role;

import eu.hexsz.werewolf.api.IllegalRequestException;
import eu.hexsz.werewolf.api.Request;
import eu.hexsz.werewolf.player.Player;

/**
 * Contains the handling for the standard player actions. Must be inherited by each role.
 * The class of the {@link PlayerController} stored in the corresponding
 * {@link Player} determines which role the player has.
 * @implNote All specific role behavior should only be in the implementation of this class and not in other classes.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public abstract class AbstractRole implements PlayerController {

    /**
     * {@inheritDoc}
     * @since 1.0-SNAPSHOT
     * */
    @Override
    public void handle(Request request) throws IllegalRequestException {
        PlayerController.super.handle(request);
    }
}
