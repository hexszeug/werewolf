package eu.hexsz.werewolf.update;

import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.time.DayPhase;
import eu.hexsz.werewolf.time.NightPhase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhaseUpdateBuilderTest {

    @Test
    void build() {
        //given
        Message message1 = new PhaseUpdateBuilder(NightPhase.WEREWOLVES).build();
        Message message2 = new PhaseUpdateBuilder().setPhase(DayPhase.DEAD_REVEAL).build();
        Message message3 = new PhaseUpdateBuilder(DayPhase.DEAD_REVEAL).setPhase(DayPhase.ACCUSING).build();

        //when


        //expect
        assertEquals("game", message1.getPath());
        assertEquals("phase", message1.getType());
        assertEquals("WEREWOLVES", message1.getData());
        assertEquals("game", message2.getPath());
        assertEquals("phase", message2.getType());
        assertEquals("DEAD_REVEAL", message2.getData());
        assertEquals("game", message3.getPath());
        assertEquals("phase", message3.getType());
        assertEquals("ACCUSING", message3.getData());
    }
}