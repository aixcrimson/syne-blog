import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class TestPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Database password
        String dbPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z3rkKKvCbe9c4fO9rQg7UbiC";

        // Test different passwords
        String[] testPasswords = {"admin123", "admin", "123456", "Syne123", "syne123", "syne", "Syne", "admin2024", "password"};

        for (String pwd : testPasswords) {
            boolean matches = encoder.matches(pwd, dbPassword);
            System.out.println("Password '" + pwd + "' matches: " + matches);
        }

        // Generate new password hash
        String newPassword = "admin123";
        String newHash = encoder.encode(newPassword);
        System.out.println("\nNew password '" + newPassword + "' hash: " + newHash);

        // SQL update statement
        System.out.println("\nSQL update statement:");
        System.out.println("UPDATE users SET password_hash = '" + newHash + "' WHERE username = 'Syne';");
    }
}