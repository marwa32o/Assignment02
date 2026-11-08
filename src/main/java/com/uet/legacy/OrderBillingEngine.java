
package com.uet.legacy;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class OrderBillingEngine {

    private String logFilePath = "order_audit.log";

    public boolean processOrder(Map<String, Object> orderData) {

        // 1. Input Validation
        if (orderData == null || orderData.isEmpty()) {
            System.out.println("Error: Order data is empty!");
            return false;
        }

        String email = (String) orderData.get("customerEmail");

        if (email == null || !email.contains("@")) {
            System.out.println("Error: Invalid customer email!");
            return false;
        }

        List<Map<String, Object>> items =
                (List<Map<String, Object>>) orderData.get("items");

        if (items == null || items.isEmpty()) {
            System.out.println("Error: Cart has no items!");
            return false;
        }

        // 2. Total and Discount Calculation
        double rawTotal = 0.0;

        for (Map<String, Object> item : items) {

            if (!item.containsKey("price")
                    || !item.containsKey("quantity")) {
                System.out.println("Error: Missing item price or quantity!");
                return false;
            }

            double price = (double) item.get("price");
            int quantity = (int) item.get("quantity");

            if (price <= 0 || quantity <= 0) {
                System.out.println("Error: Invalid price or quantity!");
                return false;
            }

            rawTotal += price * quantity;
        }

        double discount = 0.0;

        String customerType =
                (String) orderData.getOrDefault("customerType", "REGULAR");

        if (customerType.equals("VIP")) {

            if (rawTotal > 5000) {
                discount = rawTotal * 0.15;
            } else {
                discount = rawTotal * 0.10;
            }

        } else if (customerType.equals("STUDENT")) {

            discount = rawTotal * 0.08;

        } else if (customerType.equals("CORPORATE")) {

            if (items.size() >= 10) {
                discount = rawTotal * 0.20;
            } else {
                discount = rawTotal * 0.05;
            }

        } else {

            if (rawTotal > 10000) {
                discount = 500.0;
            }
        }

        double netAmount = rawTotal - discount;

        // 3. Tax Calculation
        String country =
                (String) orderData.getOrDefault("country", "PK");

        double taxRate = 0.15;

        if (country.equals("PK")) {
            taxRate = 0.17;
        } else if (country.equals("US")) {
            taxRate = 0.08;
        } else if (country.equals("UK")) {
            taxRate = 0.20;
        }

        double tax = netAmount * taxRate;
        double payableAmount = netAmount + tax;

        // 4. Payment Processing
        String paymentMethod =
                (String) orderData.getOrDefault("paymentMethod", "CASH");

        String paymentStatus = "FAILED";

        if (paymentMethod.equals("CREDIT_CARD")) {

            String cardNumber =
                    (String) orderData.getOrDefault("cardNumber", "");

            if (cardNumber.length() == 16
                    && cardNumber.matches("\\d+")) {

                System.out.println(
                        "Processing credit card payment of PKR "
                        + payableAmount);

                paymentStatus = "PAID";

            } else {
                System.out.println("Card declined: Must be 16 digits.");
                return false;
            }

        } else if (paymentMethod.equals("EASYPAISA")) {

            String mobileNo =
                    (String) orderData.getOrDefault("mobileNumber", "");

            if (mobileNo.length() == 11
                    && mobileNo.startsWith("03")) {

                System.out.println(
                        "Sending EasyPaisa OTP push to " + mobileNo
                        + " for PKR " + payableAmount);

                paymentStatus = "PAID";

            } else {
                System.out.println("EasyPaisa failed: Invalid mobile number.");
                return false;
            }

        } else if (paymentMethod.equals("CASH_ON_DELIVERY")) {

            if (payableAmount > 25000) {
                System.out.println(
                        "Cash on delivery not allowed for orders above PKR 25,000.");
                return false;
            }

            paymentStatus = "PENDING_CASH";

        } else {

            System.out.println(
                    "Error: Unsupported payment method: " + paymentMethod);

            return false;
        }

        // 5. Direct File Saving
        try (FileWriter writer =
                     new FileWriter(logFilePath, true)) {

            writer.write(
                    LocalDateTime.now()
                    + " | Order for: " + email
                    + " | Payable: PKR " + payableAmount
                    + " | Status: " + paymentStatus
                    + "\n");

        } catch (IOException e) {

            System.out.println(
                    "Failed to write order log: " + e.getMessage());
        }

        // 6. Notification
        String notifyChannel =
                (String) orderData.getOrDefault("notifyChannel", "EMAIL");

        if (notifyChannel.equals("EMAIL")) {

            System.out.println(
                    "[EMAIL DISPATCHED] To: " + email
                    + " | Your order of PKR "
                    + String.format("%.2f", payableAmount)
                    + " is confirmed!");

        } else if (notifyChannel.equals("SMS")) {

            String phone =
                    (String) orderData.getOrDefault(
                            "phone", "0300-0000000");

            System.out.println(
                    "[SMS DISPATCHED] To: " + phone
                    + " | Order confirmed! Paid: PKR "
                    + String.format("%.2f", payableAmount));
        }

        return true;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public String getLogFilePath() {
        return logFilePath;
    }
}
