package eu.hexsz.werewolf.role;

import eu.hexsz.werewolf.api.IllegalRequestException;
import eu.hexsz.werewolf.api.Request;

/**
 * Is the central control class which handles any player actions.
 * The only implementation of this should be {@link AbstractRole}.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public interface PlayerController {
    /**
     * The only implementation of this should be {@link AbstractRole#handle(Request)}.
     * @since 1.0-SNAPSHOT
     * */
    void handle(Request request) throws IllegalRequestException;
}
