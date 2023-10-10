package com.kalynx.simplethreadingservice;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.function.Supplier;

import static org.mockito.Mockito.*;

public class ThreadServiceTests {

    @Test
    void threadService_givenUntilCondition_endEarly() {

        Supplier<String> event = Mockito.mock(Supplier.class);
        Mockito.when(event.get()).thenReturn("Hello World");

        ThreadService.schedule(event).forEvery(Duration
                .ofMillis(100))
                .over(Duration.ofSeconds(1))
                .orUntil(val -> val.equals("Hello World"))
                .andWaitForCompletion();
        verify(event, times(1)).get();
    }

    @Test
    void threadService_givenNoUntilCondition_endafter1second() {

        Supplier<String> event = Mockito.mock(Supplier.class);
        Mockito.when(event.get()).thenReturn("Hello World");

        ThreadService.schedule(event).forEvery(Duration
                        .ofMillis(100))
                .over(Duration.ofSeconds(1))
                .andWaitForCompletion();
        verify(event, atLeast(10)).get();
    }

    @Test
    void threadService_givenUntilConditionThatNeverCompletes_endafter1second() {

        Supplier<String> event = Mockito.mock(Supplier.class);
        Mockito.when(event.get()).thenReturn("Hello World");

        ThreadService.schedule(event).forEvery(Duration
                        .ofMillis(100))
                .over(Duration.ofSeconds(1)).orUntil(val -> val.equals("blarg"))
                .andWaitForCompletion();
        verify(event, atLeast(10)).get();
    }
}
