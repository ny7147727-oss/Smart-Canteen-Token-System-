package com.smartcanteen.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AppThreadPool {

    private static ExecutorService orderExecutor;
    private static ScheduledExecutorService updaterExecutor;
    private static OrderProcessor orderProcessor;
    private static TokenDisplayUpdater tokenDisplayUpdater;

    private AppThreadPool() {
        // Prevent instantiation
    }

    public static synchronized void startOrderProcessor() {
        if (orderProcessor == null || !orderProcessor.isRunning()) {
            if (orderExecutor == null || orderExecutor.isShutdown()) {
                orderExecutor = Executors.newSingleThreadExecutor(r -> {
                    Thread t = new Thread(r, "OrderProcessorThread");
                    t.setDaemon(true);
                    return t;
                });
            }
            orderProcessor = new OrderProcessor();
            orderExecutor.submit(orderProcessor);
        }
    }

    public static synchronized void startTokenUpdater() {
        if (tokenDisplayUpdater == null) {
            tokenDisplayUpdater = new TokenDisplayUpdater();
        }

        if (updaterExecutor == null || updaterExecutor.isShutdown()) {
            updaterExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "TokenDisplayUpdaterThread");
                t.setDaemon(true);
                return t;
            });
            updaterExecutor.scheduleAtFixedRate(tokenDisplayUpdater, 0, 2, TimeUnit.SECONDS);
        }
    }

    public static synchronized TokenDisplayUpdater getTokenDisplayUpdater() {
        if (tokenDisplayUpdater == null) {
            tokenDisplayUpdater = new TokenDisplayUpdater();
        }
        return tokenDisplayUpdater;
    }

    public static synchronized void shutdownAll() {
        if (orderProcessor != null) {
            orderProcessor.shutdown();
            orderProcessor = null;
        }

        if (tokenDisplayUpdater != null) {
            tokenDisplayUpdater.shutdown();
            tokenDisplayUpdater = null;
        }

        if (orderExecutor != null && !orderExecutor.isShutdown()) {
            orderExecutor.shutdownNow();
            try {
                if (!orderExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                    orderExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                orderExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            orderExecutor = null;
        }

        if (updaterExecutor != null && !updaterExecutor.isShutdown()) {
            updaterExecutor.shutdownNow();
            try {
                if (!updaterExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                    updaterExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                updaterExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            updaterExecutor = null;
        }
    }
}

