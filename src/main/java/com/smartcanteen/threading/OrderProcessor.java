package com.smartcanteen.threading;

import com.smartcanteen.model.Order;
import com.smartcanteen.model.OrderStatus;
import com.smartcanteen.backend.service.OrderService;

public class OrderProcessor implements Runnable {

    private volatile boolean running = true;
    private final OrderService orderService;

    public OrderProcessor() {
        this.orderService = new OrderService();
    }

    public OrderProcessor(OrderService orderService) {
        this.orderService = (orderService != null) ? orderService : new OrderService();
    }

    @Override
    public void run() {
        OrderQueueManager queueManager = OrderQueueManager.getInstance();
        while (running) {
            try {
                // Thread blocks safely until a new confirmed paid order is enqueued
                Order order = queueManager.dequeue();
                if (order == null || order.getOrderId() <= 0) {
                    continue;
                }

                // Stage 1: Simulated kitchen queue delay (2.5 seconds)
                Thread.sleep(2500);
                if (!running) {
                    break;
                }

                // Stage 2: In-place update to PREPARING (Zero duplicate records created)
                orderService.updateOrderStatus(order.getOrderId(), OrderStatus.PREPARING);

                // Stage 3: Simulated cooking & packing time (4.5 seconds)
                Thread.sleep(4500);
                if (!running) {
                    break;
                }

                // Stage 4: In-place update to READY (Triggers live token call)
                orderService.updateOrderStatus(order.getOrderId(), OrderStatus.READY);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("OrderProcessor background exception: " + e.getMessage());
            }
        }
    }

    public void shutdown() {
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
