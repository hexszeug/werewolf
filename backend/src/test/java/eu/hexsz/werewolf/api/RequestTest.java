package eu.hexsz.werewolf.api;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {

    @Test
    void Request() {
        //given
        Object correctObj = new ArrayList<>(Arrays.asList("my-path", "my-type", 123));
        Object wrongClass = "1";
        Object toFewElements = new ArrayList<>(Arrays.asList("1"));
        Object toManyElements = new ArrayList<>(Arrays.asList("1", "2", "3", "4"));
        Object noPath = new ArrayList<>(Arrays.asList(1, "2", "3"));
        Object noType = new ArrayList<>(Arrays.asList("1", 2, "3"));

        //when


        //expect
        assertDoesNotThrow(() -> {
            Request correctReq = new Request(correctObj);
            assertEquals("my-path", correctReq.getPath());
            assertEquals("my-type", correctReq.getType());
            assertEquals(123, correctReq.getData());
        });
        assertThrows(IllegalRequestException.class, () -> {
            new Request(wrongClass);
        }, String.format("Request %s is not an array.", wrongClass));
        assertThrows(IllegalRequestException.class, () -> {
            new Request(toFewElements);
        }, String.format("Request %s has not exactly three elements.", toFewElements));
        assertThrows(IllegalRequestException.class, () -> {
            new Request(toManyElements);
        }, String.format("Request %s has not exactly three elements.", toManyElements));
        assertThrows(IllegalRequestException.class, () -> {
            new Request(noPath);
        }, String.format("The path of request %s is not a string.", noPath));
        assertThrows(IllegalRequestException.class, () -> {
            new Request(noType);
        }, String.format("The type of request %s is not a string.", noType));
    }
}