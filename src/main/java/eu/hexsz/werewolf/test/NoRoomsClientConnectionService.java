package eu.hexsz.werewolf.test;

import eu.hexsz.werewolf.GameFactory;
import eu.hexsz.werewolf.api.Session;
import eu.hexsz.werewolf.api.SessionRegistry;
import eu.hexsz.werewolf.api.Socket;
import eu.hexsz.werewolf.api.Socket_JsonWebSocket;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.util.HashMap;

public class NoRoomsClientConnectionService extends WebSocketServer {

    public static void main(String[] args) {
        new NoRoomsClientConnectionService(new SessionRegistry()).start();
    }

    private HashMap<WebSocket, Socket> clientSockets;

    //dependencies
    private final SessionRegistry sessionRegistry;

    public NoRoomsClientConnectionService(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
        clientSockets = new HashMap<>();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("opened connection");
        Session session = new Session(sessionRegistry);
        Socket_JsonWebSocket socket = new Socket_JsonWebSocket(conn, session);
        session.setSocket(socket);
        clientSockets.put(conn, socket);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Socket socket = clientSockets.get(conn);
        socket.close();
        clientSockets.remove(conn);
        System.out.println("closed connection");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if (message.equals("start")) {
            new GameFactory(sessionRegistry.getSessions()).build();
            return;
        }
        Socket socket = clientSockets.get(conn);
        socket.receive(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("error");
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("serving on " + getAddress());
    }
}
