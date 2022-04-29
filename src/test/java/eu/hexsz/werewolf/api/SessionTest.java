package eu.hexsz.werewolf.api;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionTest {

    private SessionRegistry sessionRegistry = mock(SessionRegistry.class);

    @Test
    void sessionRegistry() {
        //given
        SessionRegistry sessionRegistry = mock(SessionRegistry.class);

        //when
        Session session = new Session("test", sessionRegistry);

        //expect
        verify(sessionRegistry, times(1)).addSession(session);
    }

    @Test
    void sessionIDGeneration() {
        //given


        //when


        //expect
        assertDoesNotThrow(() -> {
            new Session(sessionRegistry);
        });
    }

    @Test
    void unbindReceiver() {
        //given
        Session session = new Session("test", sessionRegistry);
        RequestHandler requestHandler = mock(RequestHandler.class);
        session.bindReceiver("test", requestHandler);
        Object testReq = new ArrayList<>(Arrays.asList("test", "type", "data"));

        //when
        session.unbindReceiver("test");
        session.receive(testReq);

        //expect
        verify(requestHandler, times(0)).receive(any());
    }

    @Test
    void autoUnbindReceiver() {
        //given
        Session session = new Session("test", sessionRegistry);
        RequestHandler stableRequestHandler = mock(RequestHandler.class);
        RequestHandler unstableRequestHandler = new RequestHandler() {
            @Override
            public void receive(Request request) {
                stableRequestHandler.receive(request);
            }
        };
        session.bindReceiver("path", unstableRequestHandler);
        Object testReq = new ArrayList<>(Arrays.asList("test", "type", "data"));

        //when
        unstableRequestHandler = null;
        System.gc();

        //expect
        verify(stableRequestHandler, times(0)).receive(any());
        try {
            Field receiverMap = session.getClass().getDeclaredField("receiverMap");
            receiverMap.setAccessible(true);
            assertFalse(((HashMap<String, Object>) receiverMap.get(session)).containsKey("test"));
        } catch (NoSuchFieldException | IllegalAccessException e) {}
    }

    @Test
    void receive() {
        //given
        Session session = new Session("test", sessionRegistry);
        Socket socket = mock(Socket.class);
        session.setSocket(socket);
        RequestHandler requestHandler = mock(RequestHandler.class);
        session.bindReceiver("test", requestHandler);
        Object testReq = new ArrayList<>(Arrays.asList("test", "type", "data"));
        Object falseReq = new ArrayList<>(Arrays.asList("not-exist", "type", "data"));

        //when
        session.receive(testReq);
        session.receive(new Object());

        //expect
        verify(requestHandler, times(1)).receive(any(Request.class));
        verify(socket, times(1)).send(any());
    }

    @Test
    void send() {
        //given
        Session session = new Session("test", sessionRegistry);
        Socket socket = mock(Socket.class);
        session.setSocket(socket);

        //when
        session.send(new Message("path", "type", "data"));

        //expect
        verify(socket, times(1)).send(any());
    }

    @Test
    void receiveOnSessionPath() { //TODO add test
        //given


        //when


        //expect
    }
}