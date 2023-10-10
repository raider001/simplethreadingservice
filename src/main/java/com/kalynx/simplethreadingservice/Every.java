package com.kalynx.simplethreadingservice;

import java.time.Duration;
import java.util.function.Predicate;

public class Every<T> {
    private final ThreadService<T> service;
    Every(ThreadService<T> service, Duration duration) {
        this.service = service;
        service.forEvery = duration;
    }

    public Until<T> over(Duration period) {
        return new Until<>(service, period);
    }

    public Until<T> until(Predicate<T> until) {
        return new Until<>(service, until);
    }
}
