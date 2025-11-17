import javafx.application.Application;
import javafx.stage.Stage;
import util.DBConnection;
import view.LoginView;

public class Main extends Application {
    
    public static void main(String[] args) {
        System.out.println("=== Banking System Starting ===");
        
        // Initialize database
        initializeDatabase();
        
        // Launch JavaFX application
        System.out.println("Launching JavaFX application...");
        launch(args);
    }
    
    private static void initializeDatabase() {
        System.out.println("Initializing database...");
        try {
            if (DBConnection.testConnection() != null) {
                System.out.println("✓ SQLite database connected successfully");
            } else {
                System.err.println("✗ Database connection failed");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("System initialization failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Start with login view
            LoginView loginView = new LoginView(primaryStage);
            loginView.show();
            
            // Set close handler
            primaryStage.setOnCloseRequest(e -> {
                System.out.println("Application closing...");
                DBConnection.closeConnection();
            });
            
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void stop() {
        System.out.println("Application stopping...");
        DBConnection.closeConnection();
    }
}