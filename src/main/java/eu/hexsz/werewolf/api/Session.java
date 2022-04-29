package eu.hexsz.werewolf.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.lang.ref.WeakReference;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

/**
 * A {@code Session} is used to communicate with a certain client.
 * It holds a {@link Socket} which does the real communication and specifies the serialization protocol.
 * <br>{@code Session} abstracts the {@code Socket} from the rest of the game,
 * so the {@code Socket} can be replaced by a new one on runtime.
 * This might happen if a client looses connection to the server and tries to reconnect.
 * <br>{@code Session} also provides a randomly generated sessionID which can be used by the client
 * to reconnect to the same session.
 * <p>Session won't be replaced as long as the client stays connected to the application or is member of a room.
 * {@link eu.hexsz.werewolf.room.User} and {@link eu.hexsz.werewolf.player.Player} are replaced when the client
 * enters a new room / game.
 * <p>{@code Session}s also provide an API to let implementations of {@link RequestHandler} send {@link Message}s
 * and receive {@link Request}s from the client.
 * @see RequestHandler
 * @see Message
 * @see Request
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
//TODO implement receive() + test + sessionID stuff
public class Session implements RequestHandler {
    private final static String PATH = "session";

    private final @Getter @NonNull String sessionID;
    private @Setter Socket socket;
    private final HashMap<String, WeakReference<RequestHandler>> receiverMap;

    /**
     * Create new {@code Session} with a random sessionID.
     * @since 1.0-SNAPSHOT
     * */
    public Session(SessionRegistry sessionRegistry) {
        this(Base64
                .getEncoder()
                .encodeToString(
                        (UUID.randomUUID().toString() + UUID.randomUUID().toString())
                                .getBytes()
                ),
                sessionRegistry
        );
    }

    /**
     * Create new {@code Session} with given sessionID.
     * Should only be used for testing purposes. Prefer to use {@link Session#Session(SessionRegistry)}.
     * @since 1.0-SNAPSHOT
     * */
    public Session(String sessionID, SessionRegistry sessionRegistry) {
        this.sessionID = sessionID;
        sessionRegistry.addSession(this);
        receiverMap = new HashMap<>();
        bindReceiver(PATH, this);
    }

    /**
     * Binds the given receiver ({@link RequestHandler}) to the given path.
     * The {@link RequestHandler#receive(Request)} will be called
     * if the session receives a request with the specified path.
     * <p>This {@code Session} will only store a {@link WeakReference} of the {@code RequestHandler}.
     * If the {@code RequestHandler} should be garbage collected it is automatically unbound.
     * @param path The path where the receiver should be bound to.
     *             Should be lowercase with {@code -} for separating words
     *             and {@code /} for separating namespaces.
     * @param receiver An instance implementing {@link RequestHandler}
     *                 which should receive the request send to the specified path.
     * @since 1.0-SNAPSHOT
     * */
    public void bindReceiver(String path, RequestHandler receiver) {
        receiverMap.put(path, new WeakReference<>(receiver));
    }

    /**
     * Manually unbinds the receiver bound to the given path. Does nothing if no receiver is bound to the path.
     * @param path The path where the receiver which should be removed is bound to.
     * @since 1.0-SNAPSHOT
     * */
    public void unbindReceiver(String path) {
        receiverMap.remove(path);
    }

    /**
     * Should only be called by the {@link Socket} connected to this {@code Session}.
     * @param reqObject the request received by the {@link Socket}.
     * @since 1.0-SNAPSHOT
     * */
    public void receive(Object reqObject) {
        Request request;
        try {
            request = new Request(reqObject);
        } catch (Request.IllegalRequestException e) {
            send(new ErrorMessage(PATH, e));
            return;
        }
        String path = request.getPath();
        if (!receiverMap.containsKey(path)) {
            return;
        }
        RequestHandler receiver = receiverMap.get(path).get();
        if (receiver == null) {
            unbindReceiver(path);
            return;
        }
        receiver.receive(request);
    }

    /**
     * Sends a {@link Message} to the client.
     * @param message The message to send.
     * @since 1.0-SNAPSHOT
     * */
    public void send(Message message) {
        if (socket != null) {
            socket.send(message.getSerializable());
        }
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void receive(Request request) {
        //TODO implement
    }
}
