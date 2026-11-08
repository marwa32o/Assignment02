package com.uet.legacy;

import java.util.Map;

public class CashOnDeliveryPayment implements PaymentStrategy {

    @Override
    public String processPayment(
            Map<String, Object> orderData,
            double payableAmount) {

        if (payableAmount > 25000) {

            System.out.println(
                    "Cash on delivery not allowed for orders above PKR 25,000.");

            return null;
        }

        return "PENDING_CASH";
    }
}