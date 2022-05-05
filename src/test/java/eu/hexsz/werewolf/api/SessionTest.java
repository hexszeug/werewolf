package eu.hexsz.werewolf.api;

import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionTest {

    private SessionRegistry sessionRegistry = mock(SessionRegistry.class);
    private RequestHandler requestHandler = mock(RequestHandler.class);
    {
        when(requestHandler.PATH()).thenReturn("test");
    }

    @Test
    void sessionIDGeneration() {
        //given
        Session session = new Session(sessionRegistry);

        //when


        //expect
        assertTrue(session.getSessionID().matches("[A-Za-z0-9+/]{96}"));
    }

    @Test
    void registerAtRegistry() {
        //given
        SessionRegistry sessionRegistry = mock(SessionRegistry.class);

        //when
        Session session = new Session(sessionRegistry);

        //expect
        verify(sessionRegistry, times(1)).addSession(session);
    }

    private HashMap<String, WeakReference<RequestHandler>> getReceiverMap(Session session) throws NoSuchFieldException, IllegalAccessException {
        Field receiverMap = session.getClass().getDeclaredField("receiverMap");
        receiverMap.setAccessible(true);
        return (HashMap<String, WeakReference<RequestHandler>>) receiverMap.get(session);
    }

    @Test
    void bindReceiver() throws NoSuchFieldException, IllegalAccessException {
        //given
        Session session = new Session(sessionRegistry);

        //when
        session.bindReceiver(requestHandler);

        //expect
        assertEquals(requestHandler, getReceiverMap(session).get("test").get());
    }

    @Test
    void unbindReceiver() throws NoSuchFieldException, IllegalAccessException {
        //given
        Session session = new Session(sessionRegistry);
        session.bindReceiver(requestHandler);

        //when
        session.unbindReceiver("test");

        //expect
        assertFalse(getReceiverMap(session).containsKey("test"));
    }

    @Test
    void autoUnbindReceiver() throws IllegalRequestException, NoSuchFieldException, IllegalAccessException {
        //given
        Session session = new Session(sessionRegistry);
        RequestHandler stableRequestHandler = requestHandler;
        RequestHandler unstableRequestHandler = new RequestHandler() {
            @Override
            public String PATH() {
                return "test";
            }

            @Override
            public void receive(Request request) throws IllegalRequestException {
                stableRequestHandler.receive(request);
            }
        };
        session.bindReceiver(unstableRequestHandler);

        //when
        unstableRequestHandler = null;
        System.gc();
        session.receive(new ArrayList<>(Arrays.asList("test", "type", "data")));

        //expect
        verify(stableRequestHandler, times(0)).receive(any());
        assertFalse(getReceiverMap(session).containsKey("test"));
    }

    @Test
    void receive() throws IllegalRequestException {
        //given
        Session session = new Session(sessionRegistry);
        Socket socket = mock(Socket.class);
        session.setSocket(socket);
        session.bindReceiver(requestHandler);

        //when
        session.receive(new ArrayList<>(Arrays.asList("test", "type", "data")));
        session.receive(new Object());

        //expect
        verify(requestHandler, times(1)).receive(any(Request.class));
        verify(socket, times(1)).send(any());
    }

    @Test
    void send() {
        //given
        Session session = new Session(sessionRegistry);
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