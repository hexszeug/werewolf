package eu.hexsz.werewolf.update;

import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.api.Request;
import eu.hexsz.werewolf.api.Session;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.role.PlayerController;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutoPlayerUpdateServiceTest {

    private Player player1;
    private Player player3;
    private Player player2;
    private Session session1;
    private Session session2;
    private Session session3;
    private PlayerRegistry playerRegistry;

    private void prepareMocks() {
        class RoleName1 implements PlayerController {
            @Override
            public void handle(Request request) {}
        }
        class RoleName2 implements PlayerController {
            @Override
            public void handle(Request request) {}
        }
        class RoleName3 implements PlayerController {
            @Override
            public void handle(Request request) {}
        }
        player1 = mock(Player.class);
        player3 = mock(Player.class);
        player2 = mock(Player.class);
        session1 = mock(Session.class);
        session2 = mock(Session.class);
        session3 = mock(Session.class);
        when(player1.getPlayerID()).thenReturn("player1");
        when(player2.getPlayerID()).thenReturn("player2");
        when(player3.getPlayerID()).thenReturn("player3");
        when(player1.getPlayerController()).thenReturn(new RoleName1());
        when(player2.getPlayerController()).thenReturn(new RoleName2());
        when(player3.getPlayerController()).thenReturn(new RoleName3());
        when(player1.getStatus()).thenReturn(Status.SLEEPING);
        when(player2.getStatus()).thenReturn(Status.AWAKE);
        when(player3.getStatus()).thenReturn(Status.DEAD);
        when(player1.getSession()).thenReturn(session1);
        when(player2.getSession()).thenReturn(session2);
        when(player3.getSession()).thenReturn(session3);
        doAnswer((InvocationOnMock invocation) -> {
            Message message = invocation.getArgument(0, Message.class);
            assertEquals("game", message.getPath());
            assertEquals("player", message.getType());
            HashMap<String, Object> data = (HashMap<String, Object>) message.getData();
            String playerID = (String) data.get("playerID");
            assertTrue(data.containsKey("status") || data.containsKey("role"));
            if (data.containsKey("status")) {
                switch (playerID) {
                    case "player1": assertEquals("SLEEPING", data.get("status")); break;
                    case "player2": assertEquals("AWAKE", data.get("status")); break;
                    case "player3": assertEquals("DEAD", data.get("status")); break;
                    default:
                        fail(
                                "PlayerID should be either \"player1\", \"player2\" or \"player3\" but was "
                                        + playerID
                                        + "."
                        );
                }
            }
            if (data.containsKey("role")) {switch (playerID) {
                case "player1": assertEquals("ROLE_NAME1", data.get("role")); break;
                case "player2": assertEquals("ROLE_NAME2", data.get("role")); break;
                case "player3": assertEquals("ROLE_NAME3", data.get("role")); break;
                default:
                    fail(
                            "PlayerID should be either \"player1\", \"player2\" or \"player3\" but was "
                                    + playerID
                                    + "."
                    );
            }
            }
            return null;
        }).when(session1).send(any(Message.class));

        playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.iterator()).thenReturn(
                Arrays.asList(player1, player2, player3).iterator()
        );
    }

    @Test
    void onStatusChangeToSleeping() {
        //given
        prepareMocks();
        AutoPlayerUpdateService autoPlayerUpdateService = new AutoPlayerUpdateService(playerRegistry);

        //when
        autoPlayerUpdateService.onStatusChange(player1, Status.AWAKE);

        //expect
        verify(session1, times(1)).send(any(Message.class));
        verify(session2, times(1)).send(any(Message.class));
        verify(session3, times(1)).send(any(Message.class));
    }

    @Test
    void onStatusChangeToAwake() {
        //given
        prepareMocks();
        AutoPlayerUpdateService autoPlayerUpdateService = new AutoPlayerUpdateService(playerRegistry);

        //when
        autoPlayerUpdateService.onStatusChange(player2, Status.SLEEPING);

        //expect
        verify(session1, times(0)).send(any(Message.class));
        verify(session2, times(3)).send(any(Message.class));
        verify(session3, times(1)).send(any(Message.class));
    }

    @Test
    void onStatusChangeFromSleepingToDead() {
        //given
        prepareMocks();
        AutoPlayerUpdateService autoPlayerUpdateService = new AutoPlayerUpdateService(playerRegistry);

        //when
        autoPlayerUpdateService.onStatusChange(player3, Status.SLEEPING);

        //expect
        verify(session1, times(1)).send(any(Message.class));
        verify(session2, times(1)).send(any(Message.class));
        verify(session3, times(3)).send(any(Message.class));
    }

    @Test
    void onStatusChangeFromAwakeToDead() {
        //given
        prepareMocks();
        AutoPlayerUpdateService autoPlayerUpdateService = new AutoPlayerUpdateService(playerRegistry);

        //when
        autoPlayerUpdateService.onStatusChange(player3, Status.AWAKE);

        //expect
        verify(session1, times(1)).send(any(Message.class));
        verify(session2, times(1)).send(any(Message.class));
        verify(session3, times(1)).send(any(Message.class));
    }

    @Test
    void onRoleChange() {
        //given
        prepareMocks();
        AutoPlayerUpdateService autoPlayerUpdateService = new AutoPlayerUpdateService(playerRegistry);

        //when
        autoPlayerUpdateService.onRoleChange(player1);

        //expect
        verify(session1).send(any(Message.class));
    }
}