# Code Smell Audit
## Assignment 02 - Order Billing System

This audit examines the original `OrderBillingEngine.java` file before refactoring. Line numbers refer to the original baseline version (223 lines).

## 1. Code Smells

| No. | Code Smell | Original Lines | Why It Is a Problem | Refactoring Solution |
|---|---|---|---|---|
| 1 | Long Method | 14–213 | `processOrder()` handles validation, calculations, payment, file storage and notifications. This makes it difficult to understand, test and maintain. | Extract responsibilities into `OrderValidator`, `DiscountCalculator`, `TaxCalculator`, payment strategies, repository and notification classes. |
| 2 | Large Class | 10–223 | `OrderBillingEngine` contains several unrelated responsibilities, making the class difficult to maintain. | Separate billing, validation, payment, persistence and notification responsibilities into dedicated classes. |
| 3 | Long Conditional Statements | 64–89, 99–105, 116–170, 193–211 | Multiple conditional branches handle discounts, tax rates, payment methods and notification channels. Adding new cases makes the code increasingly complex. | Extract calculation logic and use the Strategy pattern for payment methods. Use separate notification implementations. |
| 4 | Tight Coupling | 4–5, 116–170, 193–211 | The engine directly handles file operations and contains payment and notification implementation details. | Introduce `OrderRepository`, `PaymentStrategy` and `NotificationService` abstractions. |
| 5 | Direct File Handling | 172–187 | `OrderBillingEngine` directly creates a `FileWriter` and manages file-writing errors. This mixes persistence with billing logic. | Move file operations into `FileOrderRepository`, implementing `OrderRepository`. |
| 6 | Magic Numbers | 66–88, 97–105, 121, 140, 156 | Values such as 0.17, 0.20, 5000 and 25000 are embedded directly in the logic. Their meaning is not immediately clear. | Replace unexplained numeric values with appropriately named constants. |
| 7 | Repeated Conditional Logic | 64–89, 99–105, 116–170 | Business rules are implemented through several branches in the same method, making future changes harder and increasing maintenance effort. | Separate discount and tax calculations and introduce dedicated payment strategy implementations. |
| 8 | Mixed Responsibilities | 16–35, 37–108, 110–170, 172–211 | Validation, calculations, payment processing, persistence and notifications are all mixed in one method. | Apply separation of concerns by extracting each responsibility into its own component. |

## 2. SOLID Principle Violations

| No. | Principle | Original Lines | Violation | Refactoring Solution |
|---|---|---|---|---|
| 1 | Single Responsibility Principle (SRP) | 10–14, 16–211 | `OrderBillingEngine` has multiple responsibilities, including validation, calculations, payment processing, file handling and notifications. It has multiple reasons to change. | Extract `OrderValidator`, `DiscountCalculator`, `TaxCalculator`, payment strategies, `OrderRepository` and notification classes. |
| 2 | Open/Closed Principle (OCP) | 116–170, 193–211 | Adding a new payment method or notification channel requires modifying existing conditional statements in `OrderBillingEngine`. | Introduce the `PaymentStrategy` and `NotificationService` abstractions with separate implementations. |
| 3 | Dependency Inversion Principle (DIP) | 4–5, 172–187 | The high-level billing class directly depends on low-level file-handling details instead of depending on a persistence abstraction. | Introduce the `OrderRepository` interface and use `FileOrderRepository` as its implementation. |

## 3. Overall Conclusion

The original `OrderBillingEngine` contained several maintainability and design problems, including long methods, excessive responsibilities, conditional complexity, tight coupling and direct file handling.

The refactoring process introduced separate calculation and validation classes, the Payment Strategy pattern, notification abstractions and the Repository pattern.

These improvements reduce the responsibilities of the original class and make the system easier to understand, test, extend and maintain.