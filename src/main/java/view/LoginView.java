package view;

import controllers.AuthController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {
    private Stage primaryStage;
    private AuthController authController;
    
    public LoginView() {
        this.authController = new AuthController();
    }
    
    public LoginView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.authController = new AuthController();
    }
    
    public void show() {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage must be set before calling show()");
        }
        
        primaryStage.setTitle("Banking System - Login");
        
        VBox loginLayout = new VBox(20);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setPadding(new Insets(50));
        loginLayout.setStyle("-fx-background-color: #f5f5f5;");
        
        Label titleLabel = new Label("Banking System");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);
        
        ComboBox<String> userTypeCombo = new ComboBox<>();
        userTypeCombo.getItems().addAll("Customer", "Bank Employee", "Administrator");
        userTypeCombo.setValue("Customer");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username (e.g., user1, teller, admin)");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (e.g., pass123, teller123, admin123)");
        Button loginButton = new Button("Login");
        Label statusLabel = new Label("");

        form.add(new Label("User Type:"), 0, 0);
        form.add(userTypeCombo, 1, 0);
        form.add(new Label("Username:"), 0, 1);
        form.add(usernameField, 1, 1);
        form.add(new Label("Password:"), 0, 2);
        form.add(passwordField, 1, 2);
        form.add(loginButton, 1, 3);
        form.add(statusLabel, 1, 4);
        
        loginButton.setOnAction(e -> {
            String userType = userTypeCombo.getValue();
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter username and password");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            
            try {
                AuthController.AuthResult result = authController.authenticate(userType, username, password);
                
                if (result.isSuccess()) {
                    statusLabel.setText("Login successful!");
                    statusLabel.setStyle("-fx-text-fill: green;");
                    navigateToDashboard(result.getUserType(), result.getUserID());
                } else {
                    statusLabel.setText(result.getMessage());
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            } catch (Exception ex) {
                statusLabel.setText("Authentication error: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
                ex.printStackTrace();
            }
        });
        
        // Demo credentials info
        VBox demoBox = new VBox(5);
        demoBox.setAlignment(Pos.CENTER);
        demoBox.getChildren().addAll(
            new Label("Demo Credentials:"),
            new Label("Customer: user1/pass123"),
            new Label("Employee: teller/teller123"), 
            new Label("Admin: admin/admin123")
        );
        demoBox.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        
        loginLayout.getChildren().addAll(titleLabel, form, demoBox);
        
        Scene scene = new Scene(loginLayout, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void navigateToDashboard(String userType, String userID) {
        switch (userType) {
            case "Customer":
                CustomerDashboard customerDashboard = new CustomerDashboard(primaryStage, userID);
                customerDashboard.show();
                break;
            case "Bank Employee":
                BankEmployeeDashboard employeeDashboard = new BankEmployeeDashboard(primaryStage, userID);
                employeeDashboard.show();
                break;
            case "Administrator":
                AdminDashboard adminDashboard = new AdminDashboard(primaryStage, userID);
                adminDashboard.show();
                break;
            default:
                System.err.println("Unknown user type: " + userType);
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}