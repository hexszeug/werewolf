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
    /**
     * {@inheritDoc}
     * */
    @Override
    public String PATH() {
        return "session";
    }

    private final @Getter String sessionID;
    private @Setter Socket socket;
    private final HashMap<String, WeakReference<RequestHandler>> receiverMap;

    //dependencies
    private final @NonNull SessionRegistry sessionRegistry;

    /**
     * Create new {@code Session} with a random sessionID.
     * @since 1.0-SNAPSHOT
     * */
    public Session(SessionRegistry sessionRegistry) {
        sessionID = Base64.getEncoder().encodeToString(
                (UUID.randomUUID().toString() + UUID.randomUUID())
                        .getBytes()
        );
        this.sessionRegistry = sessionRegistry;
        sessionRegistry.addSession(this);
        receiverMap = new HashMap<>();
        bindReceiver(this);
    }

    /**
     * Binds the given receiver ({@link RequestHandler}) to its path.
     * The {@link RequestHandler#receive(Request)} will be called
     * if the session receives a request with the specified path.
     * <p>This {@code Session} will only store a {@link WeakReference} of the {@code RequestHandler}.
     * If the {@code RequestHandler} should be garbage collected it is automatically unbound.
     * @param receiver An instance implementing {@link RequestHandler}
     *                 which should receive the request send to the specified path.
     * @since 1.0-SNAPSHOT
     * */
    public void bindReceiver(RequestHandler receiver) {
        if (receiver != null) {
            receiverMap.put(receiver.PATH(), new WeakReference<>(receiver));
        }
    }

    /**
     * Manually unbinds the receiver bound to the given path. Does nothing if no receiver is bound to the path.
     * @param path The path where the receiver which should be removed is bound to.
     * @since 1.0-SNAPSHOT
     * */
    public void unbindReceiver(String path) {
        if (path != null) {
            receiverMap.remove(path);
        }
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
        } catch (IllegalRequestException e) {
            send(new ErrorMessage(PATH(), e));
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
        try {
            receiver.receive(request);
        } catch (IllegalRequestException e) {
            send(new ErrorMessage(PATH(), e));
        }
    }

    /**
     * Sends a {@link Message} to the client.
     * @param message The message to send.
     * @since 1.0-SNAPSHOT
     * */
    public void send(Message message) {
        if (message != null && socket != null) {
            socket.send(message.getSerializable());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param request*/
    @Override
    public void receive(Request request) throws IllegalRequestException {
        Object data = request.getData();
        switch (request.getType()) {
            case "new":
                send(new Message(PATH(), "sessionID", sessionID));
                break;
            case "restore":
                if (!(data instanceof String)) {
                    throw new IllegalRequestException("Data must be a sessionID string.", request);
                }
                try {
                    Session session = sessionRegistry.getSession((String) data);
                    socket.setSession(session);
                    send(new Message(PATH(), "sessionID", session.getSessionID()));
                    sessionRegistry.removeSession(sessionID);
                } catch (NullPointerException e) {
                    send(new Message(PATH(), "sessionID", sessionID));
                }
                break;
        }
        //TODO more session request handlers
    }
}
