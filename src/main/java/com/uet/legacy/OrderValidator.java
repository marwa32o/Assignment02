
package com.uet.legacy;

import java.util.List;
import java.util.Map;

public class OrderValidator {

    public boolean validateOrder(Map<String, Object> orderData) {

        // 1. Check empty order
        if (orderData == null || orderData.isEmpty()) {
            System.out.println("Error: Order data is empty!");
            return false;
        }

        // 2. Validate email
        String email = (String) orderData.get("customerEmail");

        if (email == null || !email.contains("@")) {
            System.out.println("Error: Invalid customer email!");
            return false;
        }

        // 3. Validate cart items
        List<Map<String, Object>> items =
                (List<Map<String, Object>>) orderData.get("items");

        if (items == null || items.isEmpty()) {
            System.out.println("Error: Cart has no items!");
            return false;
        }

        // 4. Validate each item
        for (Map<String, Object> item : items) {

            if (!item.containsKey("price")
                    || !item.containsKey("quantity")) {

                System.out.println(
                        "Error: Missing item price or quantity!");

                return false;
            }

            double price = (double) item.get("price");
            int quantity = (int) item.get("quantity");

            if (price <= 0 || quantity <= 0) {

                System.out.println(
                        "Error: Invalid price or quantity!");

                return false;
            }
        }

        return true;
    }
}
