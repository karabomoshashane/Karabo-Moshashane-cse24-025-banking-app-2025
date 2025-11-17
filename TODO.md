# TODO: Fix Compilation Errors in Banking System

## Missing Methods in DAOs and Controllers

### EmployeeDAO
- [ ] Add `getEmployeeByUsername(String username)` method

### AdminController
- [ ] Add `createEmployee(String username, String password, String role)` method
- [ ] Add `deleteEmployee(String employeeID)` method
- [ ] Add `createCustomer(String username, String password, String name)` method
- [ ] Add `deleteCustomer(String customerID)` method
- [ ] Add `backupDatabase()` method
- [ ] Add `getAuditTrail()` method

### AccountController
- [ ] Add `transferFunds(String fromAccount, String toAccount, double amount)` method (alias for processTransfer)
- [ ] Add `withdrawFunds(String accountNumber, double amount)` method (alias for processWithdrawal)
- [ ] Add `openAccount(String customerID, String accountType)` method (alias for createAccount)

### EmployeeController
- [ ] Fix `createCustomer` call in BankEmployeeDashboard to provide 4 parameters

## Verification
- [ ] Run `mvn compile` to ensure all errors are resolved
- [ ] Test the application with `mvn javafx:run`
