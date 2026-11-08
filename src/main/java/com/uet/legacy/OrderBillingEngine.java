
package com.uet.legacy;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class OrderBillingEngine {

    private String logFilePath = "order_audit.log";

    // Separate calculation classes
    private final DiscountCalculator discountCalculator =
            new DiscountCalculator();

    private final TaxCalculator taxCalculator =
            new TaxCalculator();

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

        String customerType =
                (String) orderData.getOrDefault("customerType", "REGULAR");

        double discount = discountCalculator.calculateDiscount(
                customerType,
                rawTotal,
                items.size()
        );

        double netAmount = rawTotal - discount;

        // 3. Tax Calculation
        String country =
                (String) orderData.getOrDefault("country", "PK");

        double tax = taxCalculator.calculateTax(
                netAmount,
                country
        );

        double payableAmount = netAmount + tax;

        // 4. Payment Processing - Strategy Pattern
        String paymentMethod =
                (String) orderData.getOrDefault("paymentMethod", "CASH");

        PaymentStrategy paymentStrategy;

        switch (paymentMethod) {

            case "CREDIT_CARD":
                paymentStrategy = new CreditCardPayment();
                break;

            case "EASYPAISA":
                paymentStrategy = new EasyPaisaPayment();
                break;

            case "CASH_ON_DELIVERY":
                paymentStrategy = new CashOnDeliveryPayment();
                break;

            default:
                System.out.println(
                        "Error: Unsupported payment method: " + paymentMethod);
                return false;
        }

        String paymentStatus =
                paymentStrategy.processPayment(orderData, payableAmount);

        if (paymentStatus == null) {
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
