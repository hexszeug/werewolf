package eu.hexsz.werewolf.role;

import eu.hexsz.werewolf.api.IllegalRequestException;
import eu.hexsz.werewolf.api.Request;
import eu.hexsz.werewolf.player.Player;
import lombok.AllArgsConstructor;

/**
 * Contains the handling for the standard player actions. Must be inherited by each role.
 * The class of the {@link PlayerController} stored in the corresponding
 * {@link Player} determines which role the player has.
 * Also contains the implementation for handling standard requests.
 * @implNote All specific role behavior should only be in the implementation of this class and not in other classes.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
@AllArgsConstructor
public abstract class AbstractRole implements PlayerController {

    /**
     * Contains the request handling for all non role-specific requests.
     * Should be called by new implementations when the request is not role-specific.
     * @param request The request
     * @throws IllegalRequestException When the request type doesn't exist,
     * the request can't be handled or
     * additional data is missing.
     * @since 1.0-SNAPSHOT
     * */
    @Override
    public void handle(Request request) throws IllegalRequestException {
        if (request == null) {
            return;
        }
        switch (request.getType()) {
            //TODO handle day actions
            default: throw new IllegalRequestException("Request type is not a method.", request);
        }
    }
}
