package eu.hexsz.werewolf.player;

import eu.hexsz.werewolf.api.Session;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerTest {

    @Test
    void setStatus() {
        //given
        Session session = mock(Session.class);
        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        PlayerController playerController = mock(PlayerController.class);
        Player player = new Player("test", "test", "test", session, playerController,
                playerRegistry);

        //when
        //TODO test if AutoPlayerUpdateService is called

        //expect
    }
}