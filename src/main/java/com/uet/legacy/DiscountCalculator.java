
package com.uet.legacy;

public class DiscountCalculator {

    public double calculateDiscount(
            String customerType,
            double rawTotal,
            int itemCount) {

        double discount = 0.0;

        if (customerType.equals("VIP")) {

            if (rawTotal > 5000) {
                discount = rawTotal * 0.15;
            } else {
                discount = rawTotal * 0.10;
            }

        } else if (customerType.equals("STUDENT")) {

            discount = rawTotal * 0.08;

        } else if (customerType.equals("CORPORATE")) {

            if (itemCount >= 10) {
                discount = rawTotal * 0.20;
            } else {
                discount = rawTotal * 0.05;
            }

        } else {

            if (rawTotal > 10000) {
                discount = 500.0;
            }
        }

        return discount;
    }
}
