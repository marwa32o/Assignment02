
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
    // TEST 7: Valid Credit Card
    @Test
    void testValidCreditCard() throws Exception {
        Map<String, Object> order = createOrder(1000, 1);

        order.put("paymentMethod", "CREDIT_CARD");
        order.put("cardNumber", "1234567890123456");

        assertTrue(engine.processOrder(order));
        assertTrue(readLog().contains("PAID"));
    }

    // TEST 8: Invalid Credit Card
    @Test
    void testInvalidCreditCard() {
        Map<String, Object> order = createOrder(1000, 1);

        order.put("paymentMethod", "CREDIT_CARD");
        order.put("cardNumber", "12345");

        assertFalse(engine.processOrder(order));
    }

    // TEST 9: Valid EasyPaisa
    @Test
    void testValidEasyPaisa() throws Exception {
        Map<String, Object> order = createOrder(1000, 1);

        order.put("paymentMethod", "EASYPAISA");
        order.put("mobileNumber", "03001234567");

        assertTrue(engine.processOrder(order));
        assertTrue(readLog().contains("PAID"));
    }

    // TEST 10: Invalid EasyPaisa
    @Test
    void testInvalidEasyPaisa() {
        Map<String, Object> order = createOrder(1000, 1);

        order.put("paymentMethod", "EASYPAISA");
        order.put("mobileNumber", "12345");

        assertFalse(engine.processOrder(order));
    }

    // TEST 11: Cash on Delivery above limit
    @Test
    void testCashOnDeliveryAboveLimit() {
        Map<String, Object> order = createOrder(30000, 1);

        assertFalse(engine.processOrder(order));
    }

    // TEST 12: Null order
    @Test
    void testNullOrder() {
        assertFalse(engine.processOrder(null));
    }

    // TEST 13: Empty order
    @Test
    void testEmptyOrder() {
        Map<String, Object> order = new HashMap<>();

        assertFalse(engine.processOrder(order));
    }

    // TEST 14: Invalid email
    @Test
    void testInvalidEmail() {
        Map<String, Object> order = createOrder(1000, 1);
        order.put("customerEmail", "invalid-email");

        assertFalse(engine.processOrder(order));
    }

    // TEST 15: Empty cart
    @Test
    void testEmptyCart() {
        Map<String, Object> order = createOrder(1000, 1);
        order.put("items", new ArrayList<>());

        assertFalse(engine.processOrder(order));
    }

    // TEST 16: Negative price
    @Test
    void testNegativePrice() {
        Map<String, Object> order = createOrder(-100, 1);

        assertFalse(engine.processOrder(order));
    }

    // TEST 17: Zero quantity
    @Test
    void testZeroQuantity() {
        Map<String, Object> order = createOrder(1000, 0);

        assertFalse(engine.processOrder(order));
    }

    // TEST 18: Unsupported payment method
    @Test
    void testUnsupportedPaymentMethod() {
        Map<String, Object> order = createOrder(1000, 1);
        order.put("paymentMethod", "UNKNOWN");

        assertFalse(engine.processOrder(order));
    }

    // TEST 19: Missing item price
    @Test
    void testMissingItemPrice() {
        Map<String, Object> item = new HashMap<>();
        item.put("quantity", 2);

        Map<String, Object> order = createOrder(1000, 1);
        order.put("items", List.of(item));

        assertFalse(engine.processOrder(order));
    }

    // TEST 20: Missing item quantity
    @Test
    void testMissingItemQuantity() {
        Map<String, Object> item = new HashMap<>();
        item.put("price", 1000.0);

        Map<String, Object> order = createOrder(1000, 1);
        order.put("items", List.of(item));

        assertFalse(engine.processOrder(order));
    }

}
