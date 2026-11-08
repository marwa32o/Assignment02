
package com.uet.legacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderBillingEngineTest {

    private OrderBillingEngine engine;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        engine = new OrderBillingEngine();
        engine.setLogFilePath(
            tempDir.resolve("test.log").toString()
        );
    }

    private Map<String, Object> createOrder(
            double price, int quantity) {

        Map<String, Object> item = new HashMap<>();
        item.put("price", price);
        item.put("quantity", quantity);

        List<Map<String, Object>> items = new ArrayList<>();
        items.add(item);

        Map<String, Object> order = new HashMap<>();
        order.put("customerEmail", "test@gmail.com");
        order.put("items", items);
        order.put("paymentMethod", "CASH_ON_DELIVERY");
        order.put("customerType", "REGULAR");
        order.put("country", "PK");

        return order;
    }

    private String readLog() throws Exception {
        return Files.readString(
            tempDir.resolve("test.log")
        );
    }

    @Test
    void testVIPDiscountBelow5000() throws Exception {
        Map<String, Object> order = createOrder(1000, 2);
        order.put("customerType", "VIP");

        assertTrue(engine.processOrder(order));
        assertTrue(readLog().contains("Payable: PKR 2106.0"));
    }

    @Test
    void testVIPDiscountAbove5000() throws Exception {
        Map<String, Object> order = createOrder(6000, 1);
        order.put("customerType", "VIP");

        assertTrue(engine.processOrder(order));
        assertTrue(readLog().contains("Payable: PKR 5967.0"));
    }

    @Test
    void testStudentDiscount() throws Exception {
        Map<String, Object> order = createOrder(1000, 1);
        order.put("customerType", "STUDENT");

        assertTrue(engine.processOrder(order));
        assertTrue(readLog().contains("Payable: PKR 1076.4"));
    }

    @Test
    void testCorporateBulkDiscount() throws Exception {
        Map<String, Object> order = createOrder(100, 1);

        List<Map<String, Object>> items = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("price", 100.0);
            item.put("quantity", 1);
            items.add(item);
        }

        order.put("items", items);
        order.put("customerType", "CORPORATE");

        assertTrue(engine.processOrder(order));
        assertTrue(readLog().contains("Payable: PKR 936.0"));
    }

    @Test
    void testCorporateStandardDiscount() throws Exception {
        Map<String, Object> order = createOrder(1000, 1);
        order.put("customerType", "CORPORATE");

        assertTrue(engine.processOrder(order));
        assertTrue(readLog().contains("Payable: PKR 1111.5"));
    }

    @Test
    void testRegularFlatDiscount() throws Exception {
        Map<String, Object> order = createOrder(11000, 1);

        assertTrue(engine.processOrder(order));
        assertTrue(readLog().contains("Payable: PKR 12285.0"));
    }
}
