package eu.hexsz.werewolf.controller;

import eu.hexsz.werewolf.api.Session;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.player.Tag;
import eu.hexsz.werewolf.time.DayPhase;
import eu.hexsz.werewolf.time.NightPhase;
import eu.hexsz.werewolf.time.Time;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DayControllerTest {

    private Player player1;
    private Player player2;
    private Player player3;
    private Session session;
    private ExecutionService executionService;
    private Time time;
    private DayController dayController;
    private Method startPhase;

    @BeforeEach
    void setupMocks() throws NoSuchMethodException {
        player1 = mock(Player.class);
        player2 = mock(Player.class);
        player3 = mock(Player.class);
        session = mock(Session.class);
        doCallRealMethod().when(player1).getStatus();
        doCallRealMethod().when(player2).getStatus();
        doCallRealMethod().when(player3).getStatus();
        doCallRealMethod().when(player1).setStatus(any());
        doCallRealMethod().when(player2).setStatus(any());
        doCallRealMethod().when(player3).setStatus(any());
        when(player1.getSession()).thenReturn(session);
        when(player2.getSession()).thenReturn(session);
        when(player3.getSession()).thenReturn(session);
        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        executionService = mock(ExecutionService.class);
        time = mock(Time.class);
        dayController = new DayController(playerRegistry, executionService, time);
        when(playerRegistry.iterator()).thenAnswer((InvocationOnMock invocation)
                -> Arrays.asList(player1, player2, player3).iterator());
        when(playerRegistry.size()).thenReturn(3);
        when(time.isNight()).thenReturn(false);
        doAnswer((InvocationOnMock invocation) -> {
            when(time.getPhase()).thenReturn(NightPhase.WEREWOLVES);
            when(time.isNight()).thenReturn(true);
            return null;
        }).when(time).nextPhase();
        startPhase = DayController.class.getDeclaredMethod("startPhase");
        startPhase.setAccessible(true);
    }

    private void startPhase() {
        try {
            startPhase.invoke(dayController);
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail(e);
        }
    }

    @Test
    void manageDay() {
        //given
        time.nextPhase();
        Job job = mock(Job.class);

        //when
        dayController.manageDay(job);

        //expect
        verify(job).done();
    }

    @Test
    void deadReveal() {
        //given
        when(time.getPhase()).thenReturn(DayPhase.DEAD_REVEAL);
        when(player1.tags()).thenReturn(List.of(new Tag() {
            @Override
            public boolean isDeadly() {
                return true;
            }
        }));

        //when
        startPhase();

        //expect
        verify(executionService).kill(any(Job.class), eq(player1));
        verify(executionService).kill(any(), any());
    }

    @Test
    void accusing() {
        //given
        when(time.getPhase()).thenReturn(DayPhase.ACCUSING);

        //when
        startPhase();
        dayController.raiseHand(player1);
        dayController.charge(player1, player2);
        dayController.setAccuserDone(true);
        dayController.setAccusedDone(true);
        dayController.takeHandDown(player1);

        //expect
        verify(session, times(9)).send(any());
    }
}