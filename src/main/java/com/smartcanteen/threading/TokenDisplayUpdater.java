package com.smartcanteen.threading;

import com.smartcanteen.model.Order;
import com.smartcanteen.model.OrderStatus;
import com.smartcanteen.backend.service.OrderService;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class TokenDisplayUpdater implements Runnable {

    private final OrderService orderService;
    private final List<Consumer<Integer>> callbacks = new CopyOnWriteArrayList<>();
    private volatile boolean running = true;

    public TokenDisplayUpdater() {
        this.orderService = new OrderService();
    }

    public TokenDisplayUpdater(OrderService orderService) {
        this.orderService = (orderService != null) ? orderService : new OrderService();
    }

    public void registerCallback(Consumer<Integer> callback) {
        if (callback != null && !callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    public void unregisterCallback(Consumer<Integer> callback) {
        if (callback != null) {
            callbacks.remove(callback);
        }
    }

    @Override
    public void run() {
        if (!running) {
            return;
        }

        try {
            List<Order> activeOrders = orderService.getActiveOrders();
            if (activeOrders != null) {
                int latestReadyToken = -1;
                for (Order order : activeOrders) {
                    if (order.getStatus() == OrderStatus.READY) {
                        if (order.getTokenNumber() > latestReadyToken) {
                            latestReadyToken = order.getTokenNumber();
                        }
                    }
                }

                if (latestReadyToken != -1) {
                    for (Consumer<Integer> callback : callbacks) {
                        try {
                            callback.accept(latestReadyToken);
                        } catch (Exception e) {
                            System.err.println("Error executing token callback: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("TokenDisplayUpdater polling error: " + e.getMessage());
        }
    }

    public void shutdown() {
        this.running = false;
        callbacks.clear();
    }
}
