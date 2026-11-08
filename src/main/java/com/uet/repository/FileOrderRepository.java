
package com.uet.repository;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class FileOrderRepository implements OrderRepository {

    @Override
    public void saveOrder(
            String logFilePath,
            String email,
            double payableAmount,
            String paymentStatus
    ) throws IOException {

        try (FileWriter writer =
                     new FileWriter(logFilePath, true)) {

            writer.write(
                    LocalDateTime.now()
                    + " | Order for: " + email
                    + " | Payable: PKR " + payableAmount
                    + " | Status: " + paymentStatus
                    + "\n");
        }
    }
}
