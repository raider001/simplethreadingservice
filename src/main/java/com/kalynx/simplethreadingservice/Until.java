package com.kalynx.simplethreadingservice;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Until<T> {
    private final ThreadService<T> service;
    private Predicate<T> until = val -> false;


    Until(ThreadService<T> service, Duration duration) {
        this.service = service;
        service.forPeriod = duration;
    }

    Until(ThreadService<T> service, Predicate<T> until) {
        this.service = service;
        service.until = until;
    }

    public Until<T> orOver(Duration period) {
        return new Until<>(service, period);
    }

    public Until<T> orUntil(Predicate<T> until) {
        return new Until<>(service, until);
    }

    public void andWaitForCompletion() {
        service.handleThread();
        try {
            service.timerService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            service.workerService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            service.completionService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void dontWait() {
        service.handleThread();
    }
}
