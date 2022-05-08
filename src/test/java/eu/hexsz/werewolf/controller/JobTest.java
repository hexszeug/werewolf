package eu.hexsz.werewolf.controller;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class JobTest {

    @Test
    void done() {
        //given
        class StartMock {
            void method(Job job) {}
            void code(Job job) {}
        }
        class RecallMock {
            void method() {}
            void code() {}
        }
        StartMock start = mock(StartMock.class);
        RecallMock recall = mock(RecallMock.class);
        Job jobMethod = new Job("job method", start::method, recall::method);
        Job jobCode = new Job("job code", (Job jobParam) -> {
            start.code(jobParam);
        }, () -> {
            recall.code();
        });

        //when
        jobMethod.start();
        jobCode.start();

        //expect
        verify(start).method(jobMethod);
        verify(start).code(jobCode);

        //when
        jobMethod.done();
        jobCode.done();

        //expect
        verify(recall).method();
        verify(recall).code();
    }
}