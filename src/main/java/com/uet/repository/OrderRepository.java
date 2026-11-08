
package com.uet.repository;

import java.io.IOException;

public interface OrderRepository {

    void saveOrder(
            String logFilePath,
            String email,
            double payableAmount,
            String paymentStatus
    ) throws IOException;
}
