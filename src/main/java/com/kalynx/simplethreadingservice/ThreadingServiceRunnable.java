package com.kalynx.simplethreadingservice;

import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.function.Supplier;

class ThreadingServiceRunnable<T> implements Runnable {
    private final Supplier<T> runnableSupplier;
    private final Predicate<T> conditionalCheck;
    private final ThreadService<T> service;
    private Future<?> future;

    ThreadingServiceRunnable(Supplier<T> runnableSupplier, Predicate<T> conditionalCheck, ThreadService<T> service) {
        this.runnableSupplier = runnableSupplier;
        this.conditionalCheck = conditionalCheck;
        this.service = service;
    }

    void setFuture(Future<?> future) {
        this.future = future;
    }

    @Override
    public void run() {
        boolean res = conditionalCheck.test(runnableSupplier.get());
        if (!res) {
            return;
        }
        cancel();
    }

    void cancel() {
        future.cancel(false);
        service.workerService.shutdown();
        service.timerService.shutdown();
    }
}