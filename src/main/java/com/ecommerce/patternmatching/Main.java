package com.ecommerce.patternmatching;

interface Order {
    void confirm();
}

class OnlineOrder implements Order {
    public void confirm() { System.out.println("Online order confirmed"); }
}

class OfflineOrder implements Order {
    public void confirm() { System.out.println("Offline order confirmed"); }
}

public class Main {
    public static void main(String[] args) {
        Order order = new OnlineOrder();
        processOrder(order);
    }

    public static void processOrder(Order order) {
        if (order instanceof OnlineOrder online) {
            online.confirm();
        } else if (order instanceof OfflineOrder offline) {
            offline.confirm();
        }
    }
}
