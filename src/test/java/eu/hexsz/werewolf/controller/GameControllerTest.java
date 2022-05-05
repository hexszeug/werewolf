package eu.hexsz.werewolf.controller;

import eu.hexsz.werewolf.time.Time;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameControllerTest {

    @Test
    void startGame() {
        //given
        NightController nightController = mock(NightController.class);
        DayController dayController = mock(DayController.class);
        GameController gameController = new GameController(mock(Time.class), nightController, dayController);

        //when
        gameController.startGame(mock(Job.class));

        //expect
        verify(nightController, times(1)).manageNight(any());
    }
}