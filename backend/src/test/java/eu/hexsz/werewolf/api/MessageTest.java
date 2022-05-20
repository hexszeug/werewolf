package eu.hexsz.werewolf.api;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void serializable() {
        //given
        Message message = new Message("my-path", "my-type", 123);
        Message message1 = new Message("my-path", "my-type", null);

        //when
        Object msgObject = message.getSerializable();
        Object msgObject1 = message1.getSerializable();

        //expect
        assertInstanceOf(ArrayList.class, msgObject);
        assertInstanceOf(ArrayList.class, msgObject1);

        //when
        ArrayList msgList = (ArrayList) msgObject;
        ArrayList msgList1 = (ArrayList) msgObject1;

        //expect
        assertEquals("my-path", msgList.get(0));
        assertEquals("my-type", msgList.get(1));
        assertEquals(123, msgList.get(2));
        assertEquals(null, msgList1.get(2));
    }
}