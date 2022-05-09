package eu.hexsz.werewolf.player;

import eu.hexsz.werewolf.api.Session;
import eu.hexsz.werewolf.role.PlayerController;
import eu.hexsz.werewolf.update.AutoPlayerUpdateService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class PlayerTest {

    @Test
    void setStatus() {
        //given
        Session session = mock(Session.class);
        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        PlayerController playerController = mock(PlayerController.class);
        AutoPlayerUpdateService autoPlayerUpdateService = mock(AutoPlayerUpdateService.class);
        Player player = new Player("test", "test", "test", session, playerController,
                playerRegistry, autoPlayerUpdateService);

        //when
        //TODO test if AutoPlayerUpdateService is called

        //expect
    }
}