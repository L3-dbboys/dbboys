package com.dbboys.app;

import javafx.concurrent.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Centralized thread pool for background work.
 * All daemon threads — they won't prevent JVM shutdown.
 */
public final class AppExecutor {
    private static final ExecutorService IO_POOL = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("dbboys-io-" + t.getId());
        return t;
    });

    private AppExecutor() {}

    public static void runAsync(Runnable task) {
        IO_POOL.submit(task);
    }

    public static <T> Future<?> runTask(Task<T> task) {
        return IO_POOL.submit(task);
    }

    public static Future<?> submit(Runnable task) {
        return IO_POOL.submit(task);
    }

    public static void shutdown() {
        IO_POOL.shutdownNow();
    }

    public static ExecutorService getPool() {
        return IO_POOL;
    }
}
