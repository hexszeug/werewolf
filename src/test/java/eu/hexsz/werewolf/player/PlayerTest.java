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


        //expect
        assertDoesNotThrow(() ->  {
            player.setStatus(Status.AWAKE);
        });
        assertEquals(Status.AWAKE, player.getStatus());
        assertThrows(SecurityException.class, () -> {
            player.setStatus(Status.DEAD);
        });
        assertThrows(SecurityException.class, () -> {
            player.setStatus(Status.DEAD, player);
        });
        assertDoesNotThrow(() -> {
            player.setStatus(Status.DEAD, playerController);
        });
        assertEquals(Status.DEAD, player.getStatus());
    }
}