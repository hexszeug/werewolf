package eu.hexsz.werewolf.api;


import org.java_websocket.WebSocket;

/**
 * Socket which should be used to communicate with the client.
 * <br>Implementations should call {@link Session#receive(Object)} when receiving a message from the client.
 * The passed Argument must be importable by {@link Request}.
 * @see Session
 * @see Request
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public interface Socket {

    void setSession(Session session);

    void close();

    void receive(String message);

    void send(Object message);
}
