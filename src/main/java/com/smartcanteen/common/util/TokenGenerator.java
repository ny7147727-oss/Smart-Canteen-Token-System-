package com.smartcanteen.common.util;

import java.util.concurrent.atomic.AtomicInteger;

public class TokenGenerator {

    private static final AtomicInteger counter = new AtomicInteger(0);

    private TokenGenerator() {
        // Prevent instantiation
    }

    public static int next() {
        return counter.incrementAndGet();
    }

    public static void reset() {
        counter.set(0);
    }

    public static int peek() {
        return counter.get();
    }

    public static void initializeFrom(int maxInDb) {
        counter.updateAndGet(current -> Math.max(current, maxInDb));
    }
}
