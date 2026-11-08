
package com.uet.legacy;

import com.uet.repository.OrderRepository;
import com.uet.repository.FileOrderRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OrderBillingEngine {

    private String logFilePath = "order_audit.log";

    // Separate calculation classes
    private final DiscountCalculator discountCalculator =
            new DiscountCalculator();

    private final TaxCalculator taxCalculator =
            new TaxCalculator();

    // Separate validation class
    private final OrderValidator orderValidator =
            new OrderValidator();

    // Repository Pattern
    private final OrderRepository orderRepository =
            new FileOrderRepository();

    // Notification Service
    private final NotificationService notificationService =
            new NotificationService();

    public boolean processOrder(Map<String, Object> orderData) {

        // 1. Input Validation
        if (!orderValidator.validateOrder(orderData)) {
            return false;
        }

        String email = (String) orderData.get("customerEmail");

        List<Map<String, Object>> items =
                (List<Map<String, Object>>) orderData.get("items");

        // 2. Total and Discount Calculation
        double rawTotal = 0.0;

        for (Map<String, Object> item : items) {
            double price = (double) item.get("price");
            int quantity = (int) item.get("quantity");
            rawTotal += price * quantity;
        }

        String customerType =
                (String) orderData.getOrDefault("customerType", "REGULAR");

        double discount = discountCalculator.calculateDiscount(
                customerType, rawTotal, items.size());

        double netAmount = rawTotal - discount;

        // 3. Tax Calculation
        String country =
                (String) orderData.getOrDefault("country", "PK");

        double tax = taxCalculator.calculateTax(netAmount, country);

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

        // 5. Save Order using Repository Pattern
        try {
            orderRepository.saveOrder(
                    logFilePath,
                    email,
                    payableAmount,
                    paymentStatus
            );
        } catch (IOException e) {
            System.out.println(
                    "Failed to write order log: " + e.getMessage());
        }

        // 6. Send Notification using NotificationService
        String notifyChannel =
                (String) orderData.getOrDefault("notifyChannel", "EMAIL");

        String phone =
                (String) orderData.getOrDefault(
                        "phone", "0300-0000000");

        notificationService.sendNotification(
                notifyChannel,
                email,
                phone,
                payableAmount
        );

        return true;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public String getLogFilePath() {
        return logFilePath;
    }
}
