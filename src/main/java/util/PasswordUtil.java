package util;

// Placeholder import - Assumes a library like at.favre.lib.crypto.bcrypt.BCrypt is used
// In a real project, this import would need to be added to the project dependencies (e.g., Maven/Gradle).
//import at.favre.lib.crypto.bcrypt.BCrypt; 

public class PasswordUtil {
    
    // Dummy implementation for execution without a dependency, in a real scenario use BCrypt
    public static String hashPassword(String plainPassword) {
        // return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
        return "HASHED_" + plainPassword; // Placeholder
    }
    
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        // return BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword).verified;
        return hashedPassword.equals("HASHED_" + plainPassword); // Placeholder
    }
}