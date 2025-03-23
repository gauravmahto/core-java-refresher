package com.ecommerce.concurrency;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureExample {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> "Order Placed")
                .thenApply(order -> order + " & Confirmed")
                .thenAccept(System.out::println);

        System.out.println("Processing payment...");

        try {
            Thread.sleep(1000); // Wait to ensure async completes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
