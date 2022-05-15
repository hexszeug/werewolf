package eu.hexsz.werewolf.controller;

import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.role.DeathListener;
import eu.hexsz.werewolf.role.OnwDeathActive;
import eu.hexsz.werewolf.role.PlayerController;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExecutionServiceTest {

    @Test
    void kill() {
        //given
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        Player player3 = mock(Player.class);
        PlayerController playerController = mock(PlayerController.class);
        OnwDeathActive ownDeathActive = (OnwDeathActive)
                mock(PlayerController.class, withSettings().extraInterfaces(OnwDeathActive.class));
        DeathListener deathListener = (DeathListener)
                mock(PlayerController.class, withSettings().extraInterfaces(DeathListener.class, OnwDeathActive.class));
        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        Job job = mock(Job.class);
        when(playerRegistry.iterator()).thenAnswer((InvocationOnMock invocation)
                -> Arrays.asList(player1, player2, player3).iterator());
        when(player1.getPlayerController()).thenReturn(playerController);
        when(player2.getPlayerController()).thenReturn(ownDeathActive);
        when(player3.getPlayerController()).thenReturn(deathListener);
        when(player3.getStatus()).thenReturn(Status.DEAD);
        ExecutionService executionService = new ExecutionService(playerRegistry);

        //when
        doAnswer((InvocationOnMock invocation) -> {
            ExecutionService.Execution execution = invocation.getArgument(0, ExecutionService.Execution.class);
            Field pointerField = ExecutionService.ExecutionQueue.class.getDeclaredField("pointer");
            pointerField.setAccessible(true);
            switch ((Integer) pointerField.get(execution.getExecutionQueue())) {
                case 0 -> {
                    assertEquals(player1, execution.getExecutee());
                    execution.getExecutionQueue().addExecutee(player2);
                }
                case 1 -> assertEquals(player2, execution.getExecutee());
                default -> fail();
            }
            return null;
        }).when(deathListener).onDeath(any(ExecutionService.Execution.class));
        doAnswer((InvocationOnMock invocation) -> {
            invocation.getArgument(0, Job.class).done();
            return null;
        }).when(ownDeathActive).die(any(), any());
        executionService.kill(job, player1, player3);
        executionService.kill(job, (Player[]) null);
        executionService.kill(job, (Player) null);
        executionService.kill(job);

        //expect
        verify(player1).setStatus(Status.DEAD);
        verify(player2, times(0)).setStatus(any());
        verify(player3, times(0)).setStatus(any());
        verify(ownDeathActive).die(any(), any());
        verify((OnwDeathActive) deathListener, times(0)).die(any(), any());
        verify(deathListener, times(2)).onDeath(any());
        verify(job, times(4)).done();
    }
}