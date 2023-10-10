package com.kalynx.simplethreadingservice;

import java.time.Duration;
import java.util.function.Supplier;

class Runner<T> {
    private final ThreadService<T> service;

    Runner(ThreadService<T> service, Supplier<T> runnable) {
        this.service = service;
        service.runnable = runnable;
    }

    public Every<T> forEvery(Duration duration) {
        return new Every<>(service, duration);
    }
}