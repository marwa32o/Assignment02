
package com.uet.legacy;

public class NotificationService {

    public void sendNotification(
            String notifyChannel,
            String email,
            String phone,
            double payableAmount
    ) {

        if (notifyChannel.equals("EMAIL")) {

            System.out.println(
                    "[EMAIL DISPATCHED] To: " + email
                    + " | Your order of PKR "
                    + String.format("%.2f", payableAmount)
                    + " is confirmed!");

        } else if (notifyChannel.equals("SMS")) {

            System.out.println(
                    "[SMS DISPATCHED] To: " + phone
                    + " | Order confirmed! Paid: PKR "
                    + String.format("%.2f", payableAmount));
        }
    }
}
