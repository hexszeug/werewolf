package eu.hexsz.werewolf.api;

import lombok.NonNull;

/**
 * Socket which should be used to communicate with the client.
 * <br>Implementations should call {@link Socket#receive(String)} when receiving a message from the client.
 * That automatically calls {@link Session#receive(Object)} of the {@link Session} hold in {@link Socket#session}.
 * @see Session
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public abstract class Socket {
    private final @NonNull Session session;

    public Socket(Session session) {
        this.session = session;
    }

    public final void send(Object message) {
        String messageString = serialize(message);
        send(messageString);
    }

    protected abstract String serialize(Object message);

    protected abstract void send(String message);

    protected final void receive(String requestString) {
        session.receive(deserialize(requestString));
    }

    protected abstract Object deserialize(String request);
}
