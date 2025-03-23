package com.ecommerce;

import com.ecommerce.records.Product;
import com.ecommerce.sealed.CardPayment;
import com.ecommerce.sealed.Payment;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");

//        for (int i = 1; i <= 5; i++) {
//            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
//            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
//            System.out.println("i = " + i);
//        }

        Product product = new Product("Laptop", 1200.00, 10);
        System.out.println(product.name());    // Laptop
        System.out.println(product.price());   // 1200.0
        System.out.println(product.quantity());// 10

        Payment payment = new CardPayment();
        payment.pay(250.00); // Paid 250.0 via Card
    }
}