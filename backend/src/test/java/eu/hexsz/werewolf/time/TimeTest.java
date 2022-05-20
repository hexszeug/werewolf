package eu.hexsz.werewolf.time;

import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.api.Session;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimeTest {

    @Test
    void nextPhase() {
        //given
        NightPhase[] nightPhases = NightPhase.values();
        DayPhase[] dayPhases = DayPhase.values();
        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        Session session = mock(Session.class);
        Player player = mock(Player.class);
        Time time = new Time(playerRegistry);

        when(playerRegistry.iterator()).thenAnswer(
                (InvocationOnMock invocation) ->
                Arrays.asList(player).iterator()
        );
        doAnswer((InvocationOnMock invocation) -> {
            Message message = invocation.getArgument(0, Message.class);
            assertEquals("game", message.getPath());
            assertEquals("phase", message.getType());
            assertEquals(time.getPhase().toString(), message.getData());
            return null;
        }).when(session).send(any(Message.class));
        when(player.getSession()).thenReturn(session);

        //expect
        assertEquals(0, time.getNight());
        assertEquals(nightPhases[0], time.getPhase());
        verify(session, times(0)).send(any(Message.class));

        //when
        for (NightPhase i : nightPhases) {
            time.nextPhase();
        }

        //expect
        assertEquals(0, time.getNight());
        assertEquals(dayPhases[0], time.getPhase());
        verify(session, times(nightPhases.length)).send(any(Message.class));

        //when
        for (DayPhase i : dayPhases) {
            time.nextPhase();
        }

        //expect
        assertEquals(1, time.getNight());
        assertEquals(nightPhases[0], time.getPhase());
        verify(session, times(nightPhases.length + dayPhases.length)).send(any(Message.class));
    }
}