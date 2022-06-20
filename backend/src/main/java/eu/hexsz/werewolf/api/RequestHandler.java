package eu.hexsz.werewolf.api;

import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.time.Phase;
import eu.hexsz.werewolf.time.Time;

/**
 * Interface which must be implemented by classes which want
 * to receive {@link Request}s from clients.
 * <p>RequestHandles can be bind to a path using {@link Session#bindReceiver(RequestHandler)}
 * and unbind using {@link Session#unbindReceiver(String)}.
 * <br>Note that the {@code Session} would automatically unbind the receiver if it is garbage collected.
 * @see Session
 * @see Request
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public interface RequestHandler {

    /**
     * Should return the path this receiver should be bound to by a {@link Session}.
     * Should be lowercase with {@code -} for separating words
     * and {@code /} for separating namespaces.
     * @return The constant path
     * @since 1.0-SNAPSHOT
     * */
    String PATH();

    /**
     * Should only be called by {@link Session#receive(Object)} when receiving a {@link Request}
     * bound to a certain instance of a {@link RequestHandler} implementation.
     * @param request The request made.
     * @since 1.0-SNAPSHOT
     * */
    void receive(Request request) throws IllegalRequestException;

    static void checkPhase(Time time, Phase phase, Request request) throws IllegalRequestException {
        if (time.getPhase() != phase) {
            throw new IllegalRequestException(
                    String.format("Can only be invoked in the %s phase.", phase),
                    request
            );
        }
    }

    static void checkAwake(Player player, Request request) throws IllegalRequestException {
        if (player.getStatus() != Status.AWAKE) {
            throw new IllegalRequestException(
                    "Can only be invoked if the player is awake.",
                    request
            );
        }
    }
}
