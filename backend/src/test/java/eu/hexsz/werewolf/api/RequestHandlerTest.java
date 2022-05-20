package eu.hexsz.werewolf.api;

import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.time.DayPhase;
import eu.hexsz.werewolf.time.Time;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestHandlerTest {

    @Test
    void checkPhase() throws IllegalRequestException {
        //given
        Time time = mock(Time.class);
        when(time.getPhase()).thenReturn(DayPhase.DEAD_REVEAL);

        //when


        //expect
        RequestHandler.checkPhase(time, DayPhase.DEAD_REVEAL, mock(Request.class));
        assertThrows(IllegalRequestException.class, ()
                -> RequestHandler.checkPhase(time, DayPhase.ACCUSING, mock(Request.class))
        );
    }

    @Test
    void checkAwake() throws IllegalRequestException {
        //given
        Player awake = mock(Player.class);
        Player sleeping = mock(Player.class);
        Player dead = mock(Player.class);
        when(awake.getStatus()).thenReturn(Status.AWAKE);
        when(sleeping.getStatus()).thenReturn(Status.SLEEPING);
        when(dead.getStatus()).thenReturn(Status.DEAD);

        //when


        //expect
        RequestHandler.checkAwake(awake, mock(Request.class));
        assertThrows(IllegalRequestException.class, ()
                -> RequestHandler.checkAwake(sleeping, mock(Request.class))
        );
        assertThrows(IllegalRequestException.class, ()
                -> RequestHandler.checkAwake(dead, mock(Request.class))
        );
    }
}