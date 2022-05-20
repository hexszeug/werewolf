package eu.hexsz.werewolf.player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    @Test
    void getName() {
        //given
        class NiceTag extends Tag {

            @Override
            public boolean isDeadly() {
                return false;
            }
        }
        Tag niceTag = new NiceTag();
        Tag evalTag = new NiceTag() {

            @Override
            public String getName() {
                return "EvalTag";
            }

            @Override
            public boolean isDeadly() {
                return true;
            }
        };

        //when


        //expect
        assertEquals("NiceTag", niceTag.getName());
        assertFalse(niceTag.isDeadly());
        assertEquals("EvalTag", evalTag.getName());
        assertTrue(evalTag.isDeadly());
    }
}