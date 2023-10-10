package com.kalynx.simplethreadingservice;


import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ThreadService<T> {
    // Thread used to handle the timing and delegation to the worker thread.
    final ScheduledExecutorService timerService = Executors.newSingleThreadScheduledExecutor();
    // The actual worker thread pool used for handling process.
    final ExecutorService workerService = Executors.newCachedThreadPool();
    // The timeout thread used to cancel all other threads once the time is met.
    final ScheduledExecutorService completionService;
    Supplier<T> runnable;
    Duration forEvery;
    Duration forPeriod;
    Predicate<T> until = val -> false;

    // Hide CTOR
    private ThreadService(ScheduledExecutorService executor) {
        completionService = executor;
    }

    public static <T> Runner<T> schedule(Supplier<T> runnable) {
        ThreadService<T> tempThreadingService = new ThreadService<>(Executors.newSingleThreadScheduledExecutor());
        return new Runner<>(tempThreadingService, runnable);
    }

    public static <T> Runner<T> schedule(Supplier<T> runnable, ScheduledExecutorService executorService) {
        ThreadService<T> tempThreadingService = new ThreadService<>(executorService);
        return new Runner<>(tempThreadingService, runnable);
    }

    void handleThread() {
        ThreadingServiceRunnable<T> serviceRunnable = new ThreadingServiceRunnable<>(runnable, until, this);
        Future<?> future = timerService.scheduleAtFixedRate(serviceRunnable,
                0,
                forEvery.toMillis(),
                TimeUnit.MILLISECONDS);
        // Really strange and could lead to strange behaviour..
        serviceRunnable.setFuture(future);

        completionService.schedule(() -> {
            future.cancel(false);
            workerService.shutdown();
            timerService.shutdown();
        }, forPeriod.toMillis(), TimeUnit.MILLISECONDS);
    }

}
