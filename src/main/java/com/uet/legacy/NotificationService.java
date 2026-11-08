package com.uet.legacy;

public interface NotificationService {

    void sendNotification(
            String email,
            String phone,
            double payableAmount
    );
}