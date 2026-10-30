package com.smartcanteen.threading;

import com.smartcanteen.model.Order;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OrderQueueManager {

    private static final OrderQueueManager INSTANCE = new OrderQueueManager();

    private final BlockingQueue<Order> pendingOrders = new LinkedBlockingQueue<>();

    private OrderQueueManager() {
        // Prevent external instantiation
    }

    public static OrderQueueManager getInstance() {
        return INSTANCE;
    }

    public void enqueue(Order order) {
        if (order == null) {
            return;
        }
        pendingOrders.offer(order);
    }

    public Order dequeue() throws InterruptedException {
        return pendingOrders.take();
    }

    public int size() {
        return pendingOrders.size();
    }
}
