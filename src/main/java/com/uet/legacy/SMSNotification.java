package com.uet.legacy;

public class SMSNotification implements NotificationService {

    @Override
    public void sendNotification(
            String email,
            String phone,
            double payableAmount
    ) {

        System.out.println(
                "[SMS DISPATCHED] To: " + phone
                + " | Order confirmed! Paid: PKR "
                + String.format("%.2f", payableAmount));
    }
}