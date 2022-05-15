package eu.hexsz.werewolf.role.standard;

import eu.hexsz.werewolf.api.IllegalRequestException;
import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.api.Request;
import eu.hexsz.werewolf.api.Session;
import eu.hexsz.werewolf.controller.Job;
import eu.hexsz.werewolf.controller.NightController;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.time.NightPhase;
import eu.hexsz.werewolf.time.Time;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WerewolfTest {

    private Player player;
    private Werewolf werewolf;
    private Session session;
    private PlayerRegistry playerRegistry;
    private NightController nightController;

    private void prepareMocks() {
        player = mock(Player.class);
        session = mock(Session.class);
        playerRegistry = mock(PlayerRegistry.class);
        nightController = mock(NightController.class);
        Time time = mock(Time.class);
        werewolf = new Werewolf(player, playerRegistry, nightController, time);
        when(player.getPlayerID()).thenReturn("a");
        when(player.getStatus()).thenReturn(Status.AWAKE);
        when(player.getPlayerController()).thenReturn(werewolf);
        when(player.getSession()).thenReturn(session);
        when(playerRegistry.iterator()).thenAnswer(
                (InvocationOnMock invocation) ->
                Arrays.asList(player).iterator()
        );
        when(playerRegistry.getPlayer("a")).thenReturn(player);
        when(time.getPhase()).thenReturn(NightPhase.WEREWOLVES);
    }

    @Test
    void setAlarms() {
        //given
        prepareMocks();

        //when
        werewolf.setAlarms();

        //expect
        verify(nightController).setAlarm(NightPhase.WEREWOLVES, player);
    }

    @Test
    void manageNightPhase() {
        //given
        prepareMocks();
        Job job = mock(Job.class);

        //when
        werewolf.manageNightPhase(job);

        //expect
        assertEquals(job, werewolf.getJob());
    }

    @Test
    void handle() throws IllegalRequestException {
        //given
        prepareMocks();
        Job job = mock(Job.class);
        when(job.isRunning()).thenReturn(true);
        werewolf.manageNightPhase(job);
        doAnswer((InvocationOnMock invocation) -> {
            Message message = invocation.getArgument(0, Message.class);
            assertEquals("game", message.getPath());
            assertEquals("player", message.getType());
            assertInstanceOf(HashMap.class, message.getData());
            HashMap<String, Object> data = (HashMap<String, Object>) message.getData();
            assertEquals("a", data.get("playerID"));
            assertInstanceOf(ArrayList.class, data.get("tags"));
            ArrayList<Object> tags = (ArrayList<Object>) data.get("tags");
            assertEquals(1, tags.size());
            assertInstanceOf(Werewolf.WerewolfPointer.class, tags.get(0));
            Werewolf.WerewolfPointer werewolfPointer = (Werewolf.WerewolfPointer) tags.get(0);
            assertEquals("a", werewolfPointer.getPointerID());
            assertEquals("WerewolfPointer", werewolfPointer.getName());
            return null;
        }).when(session).send(any(Message.class));

        //when
        werewolf.handle(new Request(new ArrayList<>(Arrays.asList("game", "werewolf/point", "a"))));
        assertThrows(
                IllegalRequestException.class,
                () -> werewolf.handle(new Request(new ArrayList<>(Arrays.asList("game", "werewolf/point", "q"))))
        );

        //expect
        assertNull(werewolf.getCurrentTarget());
        verify(player, times(4)).getStatus();
        verify(session, times(1)).send(any(Message.class));
        verify(playerRegistry, times(1)).getPlayer("a");
        verify(playerRegistry, times(3)).iterator();
        verify(player, times(2)).getPlayerController();
        verify(job, times(1)).done();
    }

    @Test
    void werewolfVictim() {
        //given
        Werewolf.WerewolfVictim werewolfVictim = new Werewolf.WerewolfVictim();

        //when


        //expect
        assertTrue(werewolfVictim.isDeadly());
    }
}