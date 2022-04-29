package eu.hexsz.werewolf.api;

/**
 * Interface which must be implemented by classes which want
 * to receive {@link Request}s from clients.
 * <p>RequestHandles can be bind to a path using {@link Session#bindReceiver(String, RequestHandler)}
 * and unbind using {@link Session#unbindReceiver(String)}.
 * <br>Note that the {@code Session} would automatically unbind the receiver if it is garbage collected.
 * @see Session
 * @see Request
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public interface RequestHandler {

    /**
     * Should only be called by {@link Session#receive(Object)} when receiving a {@link Request}
     * bound to a certain instance of a {@link RequestHandler} implementation.
     * @param request The request made.
     * @since 1.0-SNAPSHOT
     * */
    void receive(Request request);
}
