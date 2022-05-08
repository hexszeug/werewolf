package eu.hexsz.werewolf.role;

import eu.hexsz.werewolf.api.IllegalRequestException;
import eu.hexsz.werewolf.api.Request;

/**
 * Is the central control class which handles any player actions.
 * Contains the default request handling for all roles.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public interface PlayerController {
    /**
     * Contains the request handling for all non role-specific requests.
     * Should be called by new implementations when the request is not role-specific.
     * @param request The request
     * @throws IllegalRequestException When the request type doesn't exist,
     * the request can't be handled or
     * additional data is missing.
     * @since 1.0-SNAPSHOT
     * */
    default void handle(Request request) throws IllegalRequestException {
        if (request == null) {
            return;
        }
        switch (request.getType()) {
            //TODO handle day actions
            default: throw new IllegalRequestException("Request type is not a method.", request);
        }
    }
}
