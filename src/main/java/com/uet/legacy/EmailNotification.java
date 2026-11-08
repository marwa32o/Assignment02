package com.uet.legacy;

public class EmailNotification implements NotificationService {

    @Override
    public void sendNotification(
            String email,
            String phone,
            double payableAmount
    ) {

        System.out.println(
                "[EMAIL DISPATCHED] To: " + email
                + " | Your order of PKR "
                + String.format("%.2f", payableAmount)
                + " is confirmed!");
    }
}