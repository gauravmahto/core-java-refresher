package com.ecommerce.concurrency;

public class ThreadExample {
    public static void main(String[] args) {
        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Running in: " + threadName);
        };

        Thread thread = new Thread(task);
        thread.start();

        System.out.println("Main thread: " + Thread.currentThread().getName());
    }
}
