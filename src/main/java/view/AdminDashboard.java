package view;

import java.util.List;

import controllers.AdminController;
import controllers.AuthController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Customer;
import model.Employee;

public class AdminDashboard {
    private Stage primaryStage;
    private String adminID;
    private AdminController adminController;
    private AuthController authController;

    public AdminDashboard(Stage primaryStage, String adminID) {
        this.primaryStage = primaryStage;
        this.adminID = adminID;
        this.adminController = new AdminController();
        this.authController = new AuthController();
    }

    public void show() {
        primaryStage.setTitle("Administrator Dashboard - Banking System");

        BorderPane rootLayout = new BorderPane();
        rootLayout.setPadding(new Insets(20));

        // Header
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        Label welcomeLabel = new Label("Welcome, System Administrator");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> showLoginView());
        logoutButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold;");

        header.getChildren().addAll(welcomeLabel, logoutButton);
        rootLayout.setTop(header);

        // Navigation (Left Sidebar)
        VBox navBar = new VBox(15);
        navBar.setPadding(new Insets(10));
        navBar.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #bdc3c7; -fx-border-width: 0 1 0 0;");

        Button employeesBtn = new Button("Manage Employees");
        Button customersBtn = new Button("Manage Customers");
        Button securityBtn = new Button("Security/Audit");

        employeesBtn.setPrefWidth(200);
        customersBtn.setPrefWidth(200);
        securityBtn.setPrefWidth(200);

        navBar.getChildren().addAll(employeesBtn, customersBtn, securityBtn);
        rootLayout.setLeft(navBar);

        // Content Area (Center)
        StackPane contentPane = new StackPane();
        rootLayout.setCenter(contentPane);

        // Default View
        showEmployeeView(contentPane);

        employeesBtn.setOnAction(e -> showEmployeeView(contentPane));
        customersBtn.setOnAction(e -> showCustomerView(contentPane));
        securityBtn.setOnAction(e -> showSecurityView(contentPane));

        Scene scene = new Scene(rootLayout, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ------------------------ EMPLOYEE VIEW ------------------------
    private void showEmployeeView(StackPane contentPane) {
        VBox employeeView = new VBox(20);
        employeeView.setPadding(new Insets(30));

        Label title = new Label("System Employees");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ListView<String> employeeList = new ListView<>();
        refreshEmployeeList(employeeList);

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);

        Button refreshBtn = new Button("Refresh List");
        refreshBtn.setOnAction(e -> refreshEmployeeList(employeeList));

        // Lock/Unlock Functionality
        Button lockUnlockBtn = new Button("Lock/Unlock Employee Account");
        TextField employeeIdField = new TextField();
        employeeIdField.setPromptText("Enter Employee ID to modify");

        controls.getChildren().addAll(refreshBtn, employeeIdField, lockUnlockBtn);

        // Add Employee Section
        TextField newEmpUserField = new TextField();
        newEmpUserField.setPromptText("Username");

        PasswordField newEmpPassField = new PasswordField();
        newEmpPassField.setPromptText("Password");

        TextField newEmpRoleField = new TextField();
        newEmpRoleField.setPromptText("Role");

        Button addEmpBtn = new Button("Add Employee");
        addEmpBtn.setOnAction(e -> {
            boolean ok = adminController.createEmployee(
                    newEmpUserField.getText(),
                    newEmpPassField.getText(),
                    newEmpRoleField.getText()
            );

            if (ok) {
                refreshEmployeeList(employeeList);
                newEmpUserField.clear();
                newEmpPassField.clear();
                newEmpRoleField.clear();
            }
        });

        Button deleteEmpBtn = new Button("Delete Employee");
        deleteEmpBtn.setOnAction(e -> {
            boolean ok = adminController.deleteEmployee(employeeIdField.getText());

            if (ok) {
                refreshEmployeeList(employeeList);
                employeeIdField.clear();
            }
        });

        VBox addEmpBox = new VBox(10, newEmpUserField, newEmpPassField, newEmpRoleField, addEmpBtn, deleteEmpBtn);

        employeeView.getChildren().addAll(title, employeeList, controls, addEmpBox);
        contentPane.getChildren().setAll(employeeView);
    }

    // ------------------------ CUSTOMER VIEW ------------------------
    private void showCustomerView(StackPane contentPane) {
        VBox customerView = new VBox(20);
        customerView.setPadding(new Insets(30));

        Label title = new Label("System Customers");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ListView<String> customerList = new ListView<>();
        refreshCustomerList(customerList);

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);

        Button refreshBtn = new Button("Refresh List");
        refreshBtn.setOnAction(e -> refreshCustomerList(customerList));

        // Lock/Unlock Functionality
        Button lockUnlockBtn = new Button("Lock/Unlock Customer Account");
        TextField customerIdField = new TextField();
        customerIdField.setPromptText("Enter Customer ID to modify");

        controls.getChildren().addAll(refreshBtn, customerIdField, lockUnlockBtn);

        // Add Customer Section
        TextField newCustUserField = new TextField();
        newCustUserField.setPromptText("Username");

        PasswordField newCustPassField = new PasswordField();
        newCustPassField.setPromptText("Password");

        TextField newCustNameField = new TextField();
        newCustNameField.setPromptText("Full Name");

        Button addCustBtn = new Button("Add Customer");
        addCustBtn.setOnAction(e -> {
            boolean ok = adminController.createCustomer(
                    newCustUserField.getText(),
                    newCustPassField.getText(),
                    newCustNameField.getText()
            );

            if (ok) {
                refreshCustomerList(customerList);
                newCustUserField.clear();
                newCustPassField.clear();
                newCustNameField.clear();
            }
        });

        Button deleteCustBtn = new Button("Delete Customer");
        deleteCustBtn.setOnAction(e -> {
            boolean ok = adminController.deleteCustomer(customerIdField.getText());

            if (ok) {
                refreshCustomerList(customerList);
                customerIdField.clear();
            }
        });

        VBox addCustBox = new VBox(10, newCustUserField, newCustPassField, newCustNameField, addCustBtn, deleteCustBtn);

        customerView.getChildren().addAll(title, customerList, controls, addCustBox);
        contentPane.getChildren().setAll(customerView);
    }

    // ------------------------ SECURITY / AUDIT VIEW ------------------------
    private void showSecurityView(StackPane contentPane) {
        VBox securityView = new VBox(20);
        securityView.setPadding(new Insets(30));

        Label title = new Label("Security Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // Password Reset Form
        Label resetTitle = new Label("Reset User Password");
        resetTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane resetForm = new GridPane();
        resetForm.setVgap(10);
        resetForm.setHgap(10);
        resetForm.setPadding(new Insets(10));
        resetForm.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-padding: 15;");

        Label userIdLabel = new Label("User ID (Customer/Employee):");
        TextField userIdField = new TextField();

        Label newPasswordLabel = new Label("New Password:");
        PasswordField newPasswordField = new PasswordField();

        Label userTypeLabel = new Label("User Type:");
        ComboBox<String> userTypeCombo = new ComboBox<>();
        userTypeCombo.getItems().addAll("Customer", "Employee");
        userTypeCombo.setValue("Customer");

        Button resetBtn = new Button("Reset Password");
        resetBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold;");

        Label resetStatus = new Label("");
        resetStatus.setWrapText(true);

        resetForm.addRow(0, userIdLabel, userIdField);
        resetForm.addRow(1, newPasswordLabel, newPasswordField);
        resetForm.addRow(2, userTypeLabel, userTypeCombo);
        resetForm.add(resetBtn, 1, 3);
        GridPane.setMargin(resetBtn, new Insets(10, 0, 0, 0));
        resetForm.add(resetStatus, 1, 4);

        resetBtn.setOnAction(e -> {
            String userId = userIdField.getText().trim();
            String newPassword = newPasswordField.getText().trim();
            String userType = userTypeCombo.getValue();

            if (userId.isEmpty() || newPassword.isEmpty() || userType == null) {
                resetStatus.setText("❌ Please fill all fields.");
                resetStatus.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            boolean success = adminController.resetUserPassword(userId, newPassword, userType);

            if (success) {
                resetStatus.setText("✅ Password reset successfully!");
                resetStatus.setStyle("-fx-text-fill: #27ae60;");
                userIdField.clear();
                newPasswordField.clear();
            } else {
                resetStatus.setText("❌ Failed to reset password. User ID may not exist or an internal error occurred.");
                resetStatus.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        // Backup Database Button
        Button backupBtn = new Button("Backup Database");
        backupBtn.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-font-weight: bold;");
        Label backupStatus = new Label();
        backupBtn.setOnAction(e -> {
            boolean ok = adminController.backupDatabase();
            if (ok) {
                backupStatus.setText("✅ Database backup created successfully.");
                backupStatus.setStyle("-fx-text-fill: #27ae60;");
            } else {
                backupStatus.setText("❌ Backup failed.");
                backupStatus.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        // Audit Trail
        Label auditTitle = new Label("Audit Trail");
        auditTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        ListView<String> auditList = new ListView<>();
        Button loadAuditBtn = new Button("Load Audit Trail");
        loadAuditBtn.setOnAction(e -> {
            auditList.getItems().clear();
            List<String> logs = adminController.getAuditTrail();
            auditList.getItems().addAll(logs);
        });

        securityView.getChildren().addAll(title, resetTitle, resetForm, backupBtn, backupStatus,
                auditTitle, loadAuditBtn, auditList);

        contentPane.getChildren().setAll(securityView);
    }

    // ------------------------ REFRESH METHODS ------------------------
    private void refreshEmployeeList(ListView<String> employeeList) {
        employeeList.getItems().clear();
        List<Employee> employees = adminController.getAllEmployees();
        for (Employee emp : employees) {
            employeeList.getItems().add(emp.getUserID() + " - " + emp.getUsername() + " - " + emp.getRole());
        }
    }

    private void refreshCustomerList(ListView<String> customerList) {
        customerList.getItems().clear();
        List<Customer> customers = adminController.getAllCustomers();
        for (Customer cust : customers) {
            customerList.getItems().add(cust.getUserID() + " - " + cust.getUsername());
        }
    }

    // ------------------------ LOGIN ------------------------
    private void showLoginView() {
        LoginView loginView = new LoginView(primaryStage);
        loginView.show();
    }
}
