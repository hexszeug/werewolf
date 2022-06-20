package eu.hexsz.werewolf.api;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class ErrorMessageTest {

    @Test
    void ErrorMessage() {
        //given
        class TestException extends Exception {
            public TestException(String message) {
                super(message);
            }
        }
        TestException testException = new TestException("test-message");
        ErrorMessage errorMessage = new ErrorMessage("test-path", testException);

        //when


        //expect
        assertInstanceOf(LinkedHashMap.class, errorMessage.getData());

        //when
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) errorMessage.getData();

        //expect
        assertTrue(data.containsKey("name"));
        assertEquals("TestException", data.get("name"));
        assertTrue(data.containsKey("message"));
        assertEquals("test-message", data.get("message"));
    }
}