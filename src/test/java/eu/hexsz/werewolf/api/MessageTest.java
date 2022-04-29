package eu.hexsz.werewolf.api;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void serializable() {
        //given
        Message message = new Message("my-path", "my-type", 123);

        //when
        Object msgObject = message.getSerializable();

        //expect
        assertInstanceOf(ArrayList.class, msgObject);

        //when
        ArrayList msgList = (ArrayList) msgObject;

        //expect
        assertEquals("my-path", msgList.get(0));
        assertEquals("my-type", msgList.get(1));
        assertEquals(123, msgList.get(2));
    }
}