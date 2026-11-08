package com.uet.legacy;

import java.util.Map;

public interface PaymentStrategy {

    String processPayment(
            Map<String, Object> orderData,
            double payableAmount
    );
}