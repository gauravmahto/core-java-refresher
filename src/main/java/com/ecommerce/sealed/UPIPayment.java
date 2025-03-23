package com.ecommerce.sealed;

public final class UPIPayment implements Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " via UPI");
    }
}
