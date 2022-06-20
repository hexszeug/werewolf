package eu.hexsz.werewolf.test;

import eu.hexsz.werewolf.GameFactory;
import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.api.Session;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Only used for testing
 * */
public class IOGameStart {
    protected static IOSocketService IO_SOCKET_SERVICE;

    public static void main(String[] args) throws IOException, InterruptedException {
        setupInternalSocket();
//        setupExternalSocket(1234);

        ArrayList<Session> sessions = new ArrayList<>();
        for (char c : "abc".toCharArray()) {
            IOSession session = new IOSession(String.valueOf(c), IO_SOCKET_SERVICE);
            IO_SOCKET_SERVICE.addSession(session);
            session.send(new Message("session(debug)", "sessionID", session.getSessionID()));
            sessions.add(session);
        }

        IO_SOCKET_SERVICE.startReceiving();

        new GameFactory(sessions.toArray(new Session[0])).build();

    }

    private static void setupExternalSocket(int port) throws IOException {
        Socket console = new ServerSocket(port).accept();
        IO_SOCKET_SERVICE = new IOSocketService(console.getInputStream(), console.getOutputStream());
    }

    private static void setupInternalSocket() {
        IO_SOCKET_SERVICE = new IOSocketService(System.in, System.out);
    }
}
