package com.ecommerce.sealed;

public sealed interface Payment permits CardPayment, UPIPayment {
    void pay(double amount);
}
