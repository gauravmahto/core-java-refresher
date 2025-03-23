package com.ecommerce.concurrency;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductionReadyCompletableFutureExample {

    private static final Logger logger = LoggerFactory.getLogger(ProductionReadyCompletableFutureExample.class);
    private static final Random RANDOM = new Random();
    private static final ExecutorService ORDER_PROCESSING_EXECUTOR = Executors.newFixedThreadPool(5); // Dedicated thread pool

    public static void main(String[] args) {
        logger.info("Starting order processing application...");

        String orderId = "ORD-" + RANDOM.nextInt(10000);

        // Simulate placing an order asynchronously
        CompletableFuture<String> orderPlacedFuture = CompletableFuture.supplyAsync(() -> placeOrder(orderId), ORDER_PROCESSING_EXECUTOR);

        // Asynchronously process payment after the order is placed
        CompletableFuture<String> paymentProcessedFuture = orderPlacedFuture.thenApplyAsync(ProductionReadyCompletableFutureExample::processPayment, ORDER_PROCESSING_EXECUTOR);

        // Asynchronously check inventory after payment is processed
        CompletableFuture<String> inventoryCheckedFuture = paymentProcessedFuture
                .thenApplyAsync(ProductionReadyCompletableFutureExample::checkInventory, ORDER_PROCESSING_EXECUTOR)
                .exceptionally(ex -> {
                    logger.error("Inventory check failed for order {}: {}", orderId, ex.getMessage());
                    return paymentProcessedFuture.join() + " & Inventory Check Failed"; // Recover by marking as failed
                });

        // Asynchronously prepare for shipment after inventory check
        CompletableFuture<String> shipmentPreparedFuture = inventoryCheckedFuture.thenApplyAsync(ProductionReadyCompletableFutureExample::prepareShipment, ORDER_PROCESSING_EXECUTOR);

        // Asynchronously send shipment notification
        CompletableFuture<Void> notificationSentFuture = shipmentPreparedFuture.thenAcceptAsync(ProductionReadyCompletableFutureExample::sendNotification, ORDER_PROCESSING_EXECUTOR);

        // Handle potential errors in the payment processing stage
        paymentProcessedFuture.exceptionally(ex -> {
            logger.error("Payment processing failed for order {}: {}", orderId, ex.getMessage());
            return orderPlacedFuture.join() + " & Payment Failed"; // Provide a fallback message
        }).thenAccept(result -> logger.info("Final Order Status (Payment) for {}: {}", orderId, result));

        // What happens while all this is going on in the background?
        logger.info("Continuing with other tasks while order {} is being processed asynchronously...", orderId);
        for (int i = 0; i < 3; i++) {
            logger.info("Doing something else for order {}: {}", orderId, i);
            delay(300);
        }

        // Instead of join() or sleep(), we can register a final callback
        notificationSentFuture.thenRun(() -> {
                    logger.info("Order processing completed for order: {}", orderId);
                    ORDER_PROCESSING_EXECUTOR.shutdown();
                    try {
                        if (!ORDER_PROCESSING_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                            logger.warn("Order processing executor did not terminate in the specified time.");
                            ORDER_PROCESSING_EXECUTOR.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        logger.error("Interrupted while waiting for executor termination: {}", e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                })
                .exceptionally(ex -> {
                    logger.error("Error during final notification for order {}: {}", orderId, ex.getMessage());
                    return null; // Handle the exception in the callback
                });

        logger.info("Main thread finished for order processing application.");
    }

    private static String placeOrder(String orderId) {
        logger.info("Placing order: {}", orderId);
        delay(getRandomDelay(400, 800));
        return "Order Placed (ID: " + orderId + ")";
    }

    private static String processPayment(String order) {
        logger.info("Processing payment for: {}", order);
        delay(getRandomDelay(600, 1200));
        if (RANDOM.nextDouble() < 0.9) { // Increased success rate for better flow
            return order + " & Payment Successful";
        } else {
            throw new RuntimeException("Payment Failed for order: " + order);
        }
    }

    private static String checkInventory(String paymentResult) {
        logger.info("Checking inventory for: {}", paymentResult);
        delay(getRandomDelay(500, 1000));
        return paymentResult + " & Inventory Checked";
    }

    private static String prepareShipment(String inventoryResult) {
        logger.info("Preparing shipment for: {}", inventoryResult);
        delay(getRandomDelay(700, 1400));
        return inventoryResult + " & Ready for Shipment";
    }

    private static void sendNotification(String shipmentReadyResult) {
        logger.info("Sending shipment notification for: {}", shipmentReadyResult);
        delay(getRandomDelay(300, 600));
        logger.info("Notification Sent for: {}", shipmentReadyResult);
    }

    private static void delay(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private static long getRandomDelay(long min, long max) {
        return min + (long) (RANDOM.nextDouble() * (max - min));
    }
}