package eu.hexsz.werewolf.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionRegistryTest {

    @Test
    void addSession() {
        //given
        SessionRegistry sessionRegistry = new SessionRegistry();

        //when
        Session session = new Session(sessionRegistry);

        //expect
        assertNotNull(sessionRegistry.getSession(session.getSessionID()));
    }

    @Test
    void getSession() {
        //given
        SessionRegistry sessionRegistry = new SessionRegistry();
        Session session = new Session(sessionRegistry);

        //when


        //expect
        assertEquals(session, sessionRegistry.getSession(session.getSessionID()));
    }

    @Test
    void removeSession() {
        //given
        SessionRegistry sessionRegistry = new SessionRegistry();
        Session session = new Session(sessionRegistry);

        //when
        sessionRegistry.removeSession(session.getSessionID());

        //expect
        assertNull(sessionRegistry.getSession(session.getSessionID()));
    }
}