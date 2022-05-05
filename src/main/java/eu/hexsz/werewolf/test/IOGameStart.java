package eu.hexsz.werewolf.test;

import eu.hexsz.werewolf.api.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Only used for testing
 * */
public class IOGameStart {
    protected static IOSocketService IO_SOCKET_SERVICE;

    public static void main(String[] args) throws IOException, InterruptedException {
//        setupInternalSocket();
        setupExternalSocket(1234);

        for (char c : "abc".toCharArray()) {
            IOSession session = new IOSession(String.valueOf(c), IO_SOCKET_SERVICE);
            session.send(new Message("session", "sessionID", session.getSessionID()));
        }

        IO_SOCKET_SERVICE.startReceiving();
    }

    private static void setupExternalSocket(int port) throws IOException {
        Socket console = new ServerSocket(port).accept();
        IO_SOCKET_SERVICE = new IOSocketService(console.getInputStream(), console.getOutputStream());
    }

    private static void setupInternalSocket() {
        IO_SOCKET_SERVICE = new IOSocketService(System.in, System.out);
    }
}
