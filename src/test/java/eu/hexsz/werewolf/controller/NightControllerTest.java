package eu.hexsz.werewolf.controller;

import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.role.NightActive;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.time.NightPhase;
import eu.hexsz.werewolf.time.Time;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import java.util.Arrays;

import static org.mockito.Mockito.*;

class NightControllerTest {

    private Time time;
    private Player playerA;
    private Player playerB;
    private NightActive playerControllerA;
    private NightActive playerControllerB;
    private PlayerRegistry playerRegistry;

    {
        time = mock(Time.class);
        when(time.getNight()).thenReturn(0);
        playerA = mock(Player.class);
        playerB = mock(Player.class);
        playerControllerA = mock(NightActive.class);
        playerControllerB = mock(NightActive.class);
        when(playerA.getPlayerController()).thenReturn(playerControllerA);
        when(playerB.getPlayerController()).thenReturn(playerControllerB);
        playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.iterator()).thenReturn(
                Arrays.asList(playerA, playerB).iterator()
        );
    }

    @Test
    void manageNight() {
        //given
        NightController nightController = new NightController(time, playerRegistry);

        //when
        reset(playerControllerA, playerControllerB, time);

        when(time.isNight()).thenReturn(true);
        when(time.getPhase()).thenReturn(NightPhase.WEREWOLVES);
        doAnswer((InvocationOnMock invocation) -> {

            nightController.setAlarm(NightPhase.WEREWOLVES, playerA);

            return null;
        }).when(playerControllerA).setAlarms();
        doAnswer((InvocationOnMock invocation) -> {

            nightController.setAlarm(NightPhase.WEREWOLVES, playerB);

            return null;
        }).when(playerControllerB).setAlarms();
        doAnswer((InvocationOnMock invocation) -> {

            //expect
            verify(playerControllerA).setAlarms();
            verify(playerControllerB).setAlarms();
            verify(playerA).setStatus(Status.AWAKE);
            verify(playerB).setStatus(Status.AWAKE);

            //when
            when(time.isNight()).thenReturn(false);
            invocation.getArgument(0, Job.class).done();

            return null;
        }).when(playerControllerA).manageNightPhase(any(Job.class));
        new Job("night", nightController::manageNight, () -> {

            //expect
            verify(playerControllerA).manageNightPhase(any(Job.class));
            verify(playerA).setStatus(Status.SLEEPING);
            verify(playerB).setStatus(Status.SLEEPING);

        }).start();
    }
}