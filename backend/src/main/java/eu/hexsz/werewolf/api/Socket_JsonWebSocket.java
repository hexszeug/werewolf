package eu.hexsz.werewolf.api;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.java_websocket.WebSocket;

/**
 * Implementation of {@link Socket}.
 * Uses {@link WebSocket} for sending and receiving string messages.
 * Uses {@link Gson} for serialization and deserialization of {@code Object}s.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
@AllArgsConstructor
public class Socket_JsonWebSocket implements Socket {

    private final WebSocket webSocket;
    private @Setter Session session;
    private final Gson gson = new Gson();

    @Override
    public void close() {
        session.setSocket(null);
    }

    @Override
    public void receive(String message) {
        session.receive(gson.fromJson(message, Object.class));
    }

    @Override
    public void send(Object message) {
        webSocket.send(gson.toJson(message));
    }
}
