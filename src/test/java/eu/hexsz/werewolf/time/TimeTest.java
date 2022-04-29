package eu.hexsz.werewolf.time;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeTest {

    @Test
    void nextPhase() {
        //given
        Time time = new Time();
        NightPhase[] nightPhases = NightPhase.values();
        DayPhase[] dayPhases = DayPhase.values();

        //expect
        assertEquals(0, time.getNight());
        assertEquals(nightPhases[0], time.getPhase());

        //when
        for (NightPhase i : nightPhases) {
            time.nextPhase();
        }

        //expect
        assertEquals(0, time.getNight());
        assertEquals(dayPhases[0], time.getPhase());

        //when
        for (DayPhase i : dayPhases) {
            time.nextPhase();
        }

        //expect
        assertEquals(1, time.getNight());
        assertEquals(nightPhases[0], time.getPhase());
    }
}