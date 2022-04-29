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
        };

        //when


        //expect
        assertEquals("NiceTag", niceTag.getName());
        assertEquals("EvalTag", evalTag.getName());
    }
}