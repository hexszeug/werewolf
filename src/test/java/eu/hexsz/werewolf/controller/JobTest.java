package eu.hexsz.werewolf.controller;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class JobTest {

    @Test
    void done() {
        //given
        class RecallMock {
            void method() {}
            void code() {}
        }
        RecallMock recall = mock(RecallMock.class);
        Job job = new Job("job method", recall::method);
        Job job1 = new Job("job code", () -> {
            recall.code();
        });

        //when
        job.done();
        job1.done();

        //expect
        verify(recall, times(1)).method();
        verify(recall, times(1)).code();
    }
}