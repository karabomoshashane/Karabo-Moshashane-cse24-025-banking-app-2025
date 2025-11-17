package view;

import controllers.CustomerController;
import controllers.AccountController;
import controllers.TransactionController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Account;
import model.Customer;
import model.Transaction;
import model.IInterestCalculable;
import model.InvestmentAccount;

import java.util.List;

public class CustomerDashboard {
    private Stage primaryStage;
    private String customerID;
    private CustomerController customerController;
    private AccountController accountController;
    private TransactionController transactionController;
    private VBox contentPane;
    
    public CustomerDashboard(Stage primaryStage, String customerID) {
        this.primaryStage = primaryStage;
        this.customerID = customerID;
        this.customerController = new CustomerController();
        this.accountController = new AccountController();
        this.transactionController = new TransactionController();
    }
    
    public void show() {
        Customer customer = customerController.getCustomerProfile(customerID);
        primaryStage.setTitle("Customer Dashboard - " + (customer != null ? customer.getUsername() : "Banking System"));
        
        BorderPane rootLayout = new BorderPane();
        rootLayout.setPadding(new Insets(20));
        
        // Header
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        Label welcomeLabel = new Label("Welcome, " + (customer != null ? customer.getUsername() : "Customer"));
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> showLoginView());
        
        HBox headerContent = new HBox(50, welcomeLabel, logoutBtn);
        headerContent.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().add(headerContent);
        rootLayout.setTop(header);

        // Sidebar Navigation
        VBox navBar = new VBox(10);
        navBar.setPadding(new Insets(10));
        navBar.setPrefWidth(180);
        navBar.setStyle("-fx-background-color: #ecf0f1;");
        
        Button accountsBtn = new Button("My Accounts");
        Button transferBtn = new Button("Transfer Funds");
        Button withdrawBtn = new Button("Withdraw");
        Button profileBtn = new Button("My Profile");

        accountsBtn.setMaxWidth(Double.MAX_VALUE);
        transferBtn.setMaxWidth(Double.MAX_VALUE);
        withdrawBtn.setMaxWidth(Double.MAX_VALUE);
        profileBtn.setMaxWidth(Double.MAX_VALUE);
        
        navBar.getChildren().addAll(accountsBtn, transferBtn, withdrawBtn, profileBtn);
        rootLayout.setLeft(navBar);
        
        // Main Content Area
        contentPane = new VBox(20);
        contentPane.setPadding(new Insets(20));
        rootLayout.setCenter(contentPane);
        
        // Event Handlers
        accountsBtn.setOnAction(e -> showAccountsView());
        transferBtn.setOnAction(e -> showTransferView());
        withdrawBtn.setOnAction(e -> showWithdrawView());
        profileBtn.setOnAction(e -> showProfileView());
        
        // Show default view
        showAccountsView();
        
        Scene scene = new Scene(rootLayout, 1000, 700);
        primaryStage.setScene(scene);
    }
    
    private void showLoginView() {
        LoginView loginView = new LoginView(primaryStage);
        loginView.show();
    }
    
    private void showAccountsView() {
        VBox accountsView = new VBox(15);
        Label title = new Label("My Accounts & Balances (AM-004)");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        ScrollPane scrollPane = new ScrollPane();
        VBox accountCardsContainer = new VBox(15);
        accountCardsContainer.setPadding(new Insets(5));
        
        List<Account> accounts = customerController.getCustomerAccounts(customerID);
        
        if (accounts.isEmpty()) {
            accountCardsContainer.getChildren().add(new Label("You have no accounts registered."));
        } else {
            for (Account account : accounts) {
                accountCardsContainer.getChildren().add(createAccountCard(account));
            }
        }
        
        scrollPane.setContent(accountCardsContainer);
        scrollPane.setFitToWidth(true);
        accountsView.getChildren().addAll(title, scrollPane);
        contentPane.getChildren().setAll(accountsView);
    }
    
    private VBox createAccountCard(Account account) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-color: white;");
        
        Label accountNumber = new Label("Account No: " + account.getAccountNumber());
        accountNumber.setStyle("-fx-font-weight: bold;");
        Label accountType = new Label("Type: " + account.getAccountNumber() + " (" + account.getStatus() + ")");
        Label balance = new Label(String.format("Balance: BWP %.2f", account.checkBalance()));
        balance.setStyle("-fx-font-size: 16px; -fx-text-fill: #27ae60;");

        Button transactionsBtn = new Button("View Transactions");
        transactionsBtn.setOnAction(e -> showTransactionHistoryView(account.getAccountNumber()));
        
        HBox buttonBox = new HBox(10, transactionsBtn);
        
        // Add interest calculation for interest-bearing accounts
        if (account instanceof IInterestCalculable) {
            Button interestBtn = new Button("Calculate Interest");
            interestBtn.setOnAction(e -> {
                boolean success = accountController.calculateInterest(account.getAccountNumber());
                if (success) {
                    showAccountsView(); 
                    showAlert(Alert.AlertType.INFORMATION, "Interest Calculated", 
                             "Interest has been calculated and applied to your account.");
                } else {
                    String errorMsg = "Failed to calculate interest.";
                    if (account instanceof InvestmentAccount && !((InvestmentAccount) account).isActivated()) {
                        errorMsg = "Investment account is not activated.";
                    }
                    showAlert(Alert.AlertType.ERROR, "Error", errorMsg);
                }
            });
            buttonBox.getChildren().add(interestBtn);
        }
        
        card.getChildren().addAll(accountNumber, accountType, balance, buttonBox);
        return card;
    }

    /* -------------------- TRANSFER FUNDS -------------------- */
    private void showTransferView() {
        VBox transferView = new VBox(15);
        transferView.setPadding(new Insets(20));

        Label title = new Label("Transfer Funds");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField fromField = new TextField();
        fromField.setPromptText("From Account Number");

        TextField toField = new TextField();
        toField.setPromptText("To Account Number");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        Button transferBtn = new Button("Transfer");

        transferBtn.setOnAction(e -> {
            try {
                String fromAcc = fromField.getText();
                String toAcc = toField.getText();
                double amount = Double.parseDouble(amountField.getText());

                boolean ok = accountController.transferFunds(fromAcc, toAcc, amount);

                if (ok) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Transfer Completed.");
                    showAccountsView();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Transfer failed.");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid input.");
            }
        });

        transferView.getChildren().addAll(title, fromField, toField, amountField, transferBtn);
        contentPane.getChildren().setAll(transferView);
    }

    /* -------------------- WITHDRAW MONEY -------------------- */
    private void showWithdrawView() {
        VBox withdrawView = new VBox(15);
        withdrawView.setPadding(new Insets(20));

        Label title = new Label("Withdraw Money");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField accountField = new TextField();
        accountField.setPromptText("Account Number");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        Button withdrawBtn = new Button("Withdraw");

        withdrawBtn.setOnAction(e -> {
            try {
                String acc = accountField.getText();
                double amount = Double.parseDouble(amountField.getText());

                boolean ok = accountController.withdrawFunds(acc, amount);

                if (ok) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Withdrawal completed.");
                    showAccountsView();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Withdrawal failed.");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid input.");
            }
        });

        withdrawView.getChildren().addAll(title, accountField, amountField, withdrawBtn);
        contentPane.getChildren().setAll(withdrawView);
    }

    /* -------------------- TRANSACTION HISTORY (WITH REFRESH) -------------------- */
    private void showTransactionHistoryView(String accountNumber) {
        VBox historyView = new VBox(15);
        historyView.setPadding(new Insets(10));

        Label title = new Label("Transaction History for Account: " + accountNumber);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ListView<String> transactionList = new ListView<>();
        loadTransactions(accountNumber, transactionList);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadTransactions(accountNumber, transactionList));

        HBox topBar = new HBox(10, title, refreshBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        historyView.getChildren().addAll(topBar, transactionList);
        contentPane.getChildren().setAll(historyView);
    }

    private void loadTransactions(String accountNumber, ListView<String> listView) {
        listView.getItems().clear();
        List<Transaction> transactions = customerController.getAccountTransactions(accountNumber);
        if (transactions.isEmpty()) {
            listView.getItems().add("No transactions found.");
            return;
        }

        for (Transaction tx : transactions) {
            listView.getItems().add(tx.getTransactionDetails());
        }
    }

    /* -------------------- PROFILE VIEW -------------------- */
    private void showProfileView() {
        VBox profileView = new VBox(15);
        profileView.setPadding(new Insets(20));

        Customer customer = customerController.getCustomerProfile(customerID);

        Label title = new Label("My Profile");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label username = new Label("Username: " + customer.getUsername());
        Label name = new Label("Name: " + customer.getCustomerName());
        Label id = new Label("Customer ID: " + customer.getUserID());

        Button refreshBtn = new Button("Refresh Profile");
        refreshBtn.setOnAction(e -> showProfileView());

        profileView.getChildren().addAll(title, username, name, id, refreshBtn);
        contentPane.getChildren().setAll(profileView);
    }

    /* -------------------- ALERT UTILITY -------------------- */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
