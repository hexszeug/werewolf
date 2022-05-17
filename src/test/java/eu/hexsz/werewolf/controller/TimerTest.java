package eu.hexsz.werewolf.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TimerTest {

    @Test
    void start() {
        //given
        startTime = new Date().getTime();
        timer = new Timer("test", 1, this::done);

        //when
        timer.start();

        //expect
        assertTrue(timer.isRunning());
        assertFalse(timer.isDone());
    }

    private Timer timer;
    private long startTime;

    private void done() {
        //expect
        assertEquals(1, new Date().getTime() - startTime);
        assertFalse(timer.isRunning());
        assertTrue(timer.isDone());
    }

    @Test
    void cancel() {
        //given
        Timer timer = new Timer("test", 1, Assertions::fail);

        //when
        timer.start();
        timer.cancel();

        //expect
        assertFalse(timer.isRunning());
        assertFalse(timer.isDone());
    }
}