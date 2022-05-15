package eu.hexsz.werewolf.player;

import eu.hexsz.werewolf.api.IllegalRequestException;
import eu.hexsz.werewolf.api.Request;
import eu.hexsz.werewolf.api.Session;
import eu.hexsz.werewolf.role.PlayerController;
import eu.hexsz.werewolf.update.AutoPlayerUpdateService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class PlayerTest {

    @Test
    void setStatusAndPlayerController() {
        //given
        PlayerController playerController = mock(PlayerController.class);
        AutoPlayerUpdateService autoPlayerUpdateService = mock(AutoPlayerUpdateService.class);
        Player player = new Player(
                "test",
                "test",
                "test",
                mock(Session.class),
                mock(PlayerRegistry.class),
                autoPlayerUpdateService
        );

        //when
        player.setPlayerController(playerController);
        player.setStatus(Status.AWAKE);
        player.setPlayerController(mock(PlayerController.class));

        //expect
        verify(autoPlayerUpdateService, times(0)).onPlayerCreated(player);
        verify(autoPlayerUpdateService).onStatusChange(player, Status.SLEEPING);
        verify(autoPlayerUpdateService).onRoleChange(player, null);
        verify(autoPlayerUpdateService).onRoleChange(player, playerController);
    }

    @Test
    void receive() throws IllegalRequestException {
        //given
        PlayerController playerController = mock(PlayerController.class);
        Player player = new Player("a", "", "",
                mock(Session.class), mock(PlayerRegistry.class), mock(AutoPlayerUpdateService.class));
        player.setPlayerController(playerController);
        Request request = mock(Request.class);

        //when
        player.receive(request);

        //expect
        verify(playerController).handle(request);
    }
}