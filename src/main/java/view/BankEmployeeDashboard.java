package view;

import controllers.EmployeeController;
import controllers.AccountController;
import controllers.TransactionController;
import controllers.AuthController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Account;
import model.Customer;

import java.util.List;

public class BankEmployeeDashboard {
    private Stage primaryStage;
    private String employeeID;
    private EmployeeController employeeController;
    private AccountController accountController;
    private TransactionController transactionController;
    private AuthController authController;

    public BankEmployeeDashboard(Stage primaryStage, String employeeID) {
        this.primaryStage = primaryStage;
        this.employeeID = employeeID;
        this.employeeController = new EmployeeController();
        this.accountController = new AccountController();
        this.transactionController = new TransactionController();
        this.authController = new AuthController();
    }

    public void show() {
        primaryStage.setTitle("Bank Employee Dashboard - Banking System");

        BorderPane rootLayout = new BorderPane();
        rootLayout.setPadding(new Insets(20));

        // Header
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        Label welcomeLabel = new Label("Welcome, Bank Employee " + employeeID);
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> showLoginView());
        logoutButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold;");

        header.getChildren().addAll(welcomeLabel, logoutButton);
        rootLayout.setTop(header);

        // Navigation (Left Sidebar)
        VBox navBar = new VBox(15);
        navBar.setPadding(new Insets(10));
        navBar.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #bdc3c7; -fx-border-width: 0 1 0 0;");

        Button operationsBtn = new Button("Account Operations");
        Button accountsBtn = new Button("View All Accounts");
        Button customersBtn = new Button("View All Customers");

        operationsBtn.setPrefWidth(200);
        accountsBtn.setPrefWidth(200);
        customersBtn.setPrefWidth(200);

        navBar.getChildren().addAll(operationsBtn, accountsBtn, customersBtn);
        rootLayout.setLeft(navBar);

        // Content Area (Center)
        StackPane contentPane = new StackPane();
        rootLayout.setCenter(contentPane);

        // Default View
        showOperationsView(contentPane);

        operationsBtn.setOnAction(e -> showOperationsView(contentPane));
        accountsBtn.setOnAction(e -> showAccountsView(contentPane));
        customersBtn.setOnAction(e -> showCustomersView(contentPane));

        Scene scene = new Scene(rootLayout, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ------------------------ ACCOUNTS VIEW ------------------------
    private void showAccountsView(StackPane contentPane) {
        VBox accountsView = new VBox(20);
        accountsView.setPadding(new Insets(30));

        Label title = new Label("All System Accounts");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ListView<String> accountList = new ListView<>();
        refreshAccountList(accountList);

        Button refreshBtn = new Button("Refresh List");
        refreshBtn.setOnAction(e -> refreshAccountList(accountList));

        // Open Account Section
        TextField newAccCustomerId = new TextField();
        newAccCustomerId.setPromptText("Customer ID");
        TextField newAccType = new TextField();
        newAccType.setPromptText("Account Type (Checking/Savings)");
        Button openAccBtn = new Button("Open Account");
        openAccBtn.setOnAction(e -> {
            boolean ok = accountController.openAccount(newAccCustomerId.getText(), newAccType.getText());
            if (ok) {
                refreshAccountList(accountList);
                newAccCustomerId.clear();
                newAccType.clear();
            }
        });

        // Close Account Section
        TextField closeAccField = new TextField();
        closeAccField.setPromptText("Account Number to Close");
        Button closeAccBtn = new Button("Close Account");
        closeAccBtn.setOnAction(e -> {
            boolean ok = accountController.closeAccount(closeAccField.getText());
            if (ok) {
                refreshAccountList(accountList);
                closeAccField.clear();
            }
        });

        HBox openCloseBox = new HBox(10, newAccCustomerId, newAccType, openAccBtn, closeAccField, closeAccBtn);
        openCloseBox.setAlignment(Pos.CENTER_LEFT);

        accountsView.getChildren().addAll(title, accountList, refreshBtn, openCloseBox);
        contentPane.getChildren().setAll(accountsView);
    }

    private void refreshAccountList(ListView<String> accountList) {
        accountList.getItems().clear();
        List<Account> accounts = accountController.getAllAccounts();
        for (Account acc : accounts) {
            String details = String.format("%s (%s) - Balance: $%.2f - Customer: %s",
                    acc.getAccountNumber(),
                    acc.getClass().getSimpleName(),
                    acc.checkBalance(),
                    acc.getCustomer().getUserID());
            accountList.getItems().add(details);
        }
    }

    // ------------------------ CUSTOMERS VIEW ------------------------
    private void showCustomersView(StackPane contentPane) {
        VBox customersView = new VBox(20);
        customersView.setPadding(new Insets(30));

        Label title = new Label("All System Customers");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ListView<String> customerList = new ListView<>();
        refreshCustomerList(customerList);

        Button refreshBtn = new Button("Refresh List");
        refreshBtn.setOnAction(e -> refreshCustomerList(customerList));

        // Create Customer Section
        TextField newCustUsername = new TextField();
        newCustUsername.setPromptText("Username");
        TextField newCustName = new TextField();
        newCustName.setPromptText("Full Name");
        PasswordField newCustPassword = new PasswordField();
        newCustPassword.setPromptText("Password");
        Button addCustBtn = new Button("Create Customer");
        addCustBtn.setOnAction(e -> {
            boolean ok = employeeController.createCustomer("CUST" + System.currentTimeMillis(), newCustUsername.getText(), newCustName.getText(), newCustPassword.getText());
            if (ok) {
                refreshCustomerList(customerList);
                newCustUsername.clear();
                newCustPassword.clear();
                newCustName.clear();
            }
        });

        HBox createCustBox = new HBox(10, newCustUsername, newCustPassword, newCustName, addCustBtn);
        createCustBox.setAlignment(Pos.CENTER_LEFT);

        customersView.getChildren().addAll(title, customerList, refreshBtn, createCustBox);
        contentPane.getChildren().setAll(customersView);
    }

    private void refreshCustomerList(ListView<String> customerList) {
        customerList.getItems().clear();
        List<Customer> customers = employeeController.getAllCustomers();
        for (Customer cust : customers) {
            customerList.getItems().add(cust.getUserID() + " - " + cust.getUsername());
        }
    }

    // ------------------------ OPERATIONS VIEW ------------------------
    private void showOperationsView(StackPane contentPane) {
        VBox operationsView = new VBox(20);
        operationsView.setPadding(new Insets(30));

        Label title = new Label("Customer Account Operations");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Deposit Tab
        Tab depositTab = new Tab("Process Deposit");
        VBox depositContent = new VBox(15);
        depositContent.setPadding(new Insets(20));

        GridPane depositForm = new GridPane();
        depositForm.setHgap(10);
        depositForm.setVgap(10);

        TextField depositAccountField = new TextField();
        depositAccountField.setPromptText("Account Number");
        TextField depositAmountField = new TextField();
        depositAmountField.setPromptText("Amount to Deposit");
        Button depositBtn = new Button("Process Deposit");
        Label depositResult = new Label();
        depositResult.setStyle("-fx-font-weight: bold;");

        depositForm.addRow(0, new Label("Account No:"), depositAccountField);
        depositForm.addRow(1, new Label("Amount:"), depositAmountField);
        depositForm.addRow(2, depositBtn);

        depositBtn.setOnAction(e -> {
            String accountNumber = depositAccountField.getText();
            String amountText = depositAmountField.getText();
            depositResult.setText("");

            if (accountNumber.isEmpty() || amountText.isEmpty()) {
                depositResult.setText("Please fill all fields.");
                return;
            }

            try {
                double amount = Double.parseDouble(amountText);
                boolean success = employeeController.processDeposit(accountNumber, amount);

                if (success) {
                    depositResult.setText("Deposit successful: $" + amount);
                    depositResult.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    depositAccountField.clear();
                    depositAmountField.clear();
                } else {
                    depositResult.setText("Deposit failed. Check account number.");
                    depositResult.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            } catch (NumberFormatException ex) {
                depositResult.setText("Invalid amount format.");
                depositResult.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
        });

        depositContent.getChildren().addAll(depositForm, depositResult);
        depositTab.setContent(depositContent);

        // Withdrawal Tab
        Tab withdrawalTab = new Tab("Process Withdrawal");
        VBox withdrawalContent = new VBox(15);
        withdrawalContent.setPadding(new Insets(20));

        GridPane withdrawalForm = new GridPane();
        withdrawalForm.setHgap(10);
        withdrawalForm.setVgap(10);

        TextField withdrawalAccountField = new TextField();
        withdrawalAccountField.setPromptText("Account Number");
        TextField withdrawalAmountField = new TextField();
        withdrawalAmountField.setPromptText("Amount to Withdraw");
        Button withdrawalBtn = new Button("Process Withdrawal");
        Label withdrawalResult = new Label();
        withdrawalResult.setStyle("-fx-font-weight: bold;");

        withdrawalForm.addRow(0, new Label("Account No:"), withdrawalAccountField);
        withdrawalForm.addRow(1, new Label("Amount:"), withdrawalAmountField);
        withdrawalForm.addRow(2, withdrawalBtn);

        withdrawalBtn.setOnAction(e -> {
            String accountNumber = withdrawalAccountField.getText();
            String amountText = withdrawalAmountField.getText();
            withdrawalResult.setText("");

            if (accountNumber.isEmpty() || amountText.isEmpty()) {
                withdrawalResult.setText("Please fill all fields.");
                withdrawalResult.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                return;
            }

            try {
                double amount = Double.parseDouble(amountText);
                boolean success = employeeController.processWithdrawal(accountNumber, amount);

                if (success) {
                    withdrawalResult.setText("Withdrawal successful: $" + amount);
                    withdrawalResult.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    withdrawalAccountField.clear();
                    withdrawalAmountField.clear();
                } else {
                    withdrawalResult.setText("Withdrawal failed. Check balance or account type.");
                    withdrawalResult.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            } catch (NumberFormatException ex) {
                withdrawalResult.setText("Invalid amount format.");
                withdrawalResult.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
        });

        withdrawalContent.getChildren().addAll(withdrawalForm, withdrawalResult);
        withdrawalTab.setContent(withdrawalContent);

        tabPane.getTabs().addAll(depositTab, withdrawalTab);
        operationsView.getChildren().addAll(title, tabPane);
        contentPane.getChildren().setAll(operationsView);
    }

    private void showLoginView() {
        LoginView loginView = new LoginView(primaryStage);
        loginView.show();
    }
}
