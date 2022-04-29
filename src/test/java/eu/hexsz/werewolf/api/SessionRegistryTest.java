package eu.hexsz.werewolf.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionRegistryTest {

    @Test
    void getSession() {
        //given
        SessionRegistry sessionRegistry = new SessionRegistry();
        Session session = new Session("test", sessionRegistry);

        //when


        //expect
        assertEquals(session, sessionRegistry.getSession("test"));
    }

    @Test
    void removeSession() {
        //given
        SessionRegistry sessionRegistry = new SessionRegistry();
        Session session = new Session("test", sessionRegistry);

        //when
        sessionRegistry.removeSession("test");

        //
        assertThrows(NullPointerException.class, () -> {
           sessionRegistry.getSession("test");
        });
    }
}