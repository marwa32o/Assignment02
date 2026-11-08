
# Code Smell Audit
## Assignment 02 - Order Billing System

### 1. Long Method
The processOrder() method performs multiple operations,
including validation, discount calculation, tax calculation,
payment processing, file saving and notifications.

### 2. Large Class
OrderBillingEngine has too many responsibilities.
It handles billing, payment, notification and file operations.

### 3. Long Conditional Statements
Multiple if-else statements are used for customer discounts,
tax rates and payment methods.

### 4. Tight Coupling
Payment and notification logic are directly implemented
inside OrderBillingEngine.

### 5. Single Responsibility Principle Violation
The class has several reasons to change because it handles
multiple unrelated tasks.

### 6. Open/Closed Principle Violation
Adding a new payment method requires modifying the existing
OrderBillingEngine class.

### 7. Direct File Handling
The class directly uses FileWriter to save order information.
File storage should be handled by a separate component.

### 8. Magic Numbers
Values such as 0.17, 0.20, 5000 and 25000 are written
directly in the code instead of being named constants.

## Conclusion
The legacy OrderBillingEngine class contains several
maintainability and design problems. Refactoring it into
smaller components will improve readability, flexibility,
testability and maintainability.
