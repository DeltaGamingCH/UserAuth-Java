package zngr;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Account extends DatabaseAPI {

    private static final String USERS_TABLE = "users";
    private static final String EMAIL_FIELD = "email";
    private static final String PASSWORD_FIELD = "password";
    private static final String SALT_FIELD = "salt";

    public void initAccount() { // Create users table, email, hashed password, salt
        String fields = "userid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "salt TEXT";
        createTable(USERS_TABLE, fields);
    }

    public void addAccount(String email, String password) { // Create new user with email, hashed password, salt
        try {
            String salt = PasswordHasher.generateSalt();

            String hashedPassword = PasswordHasher.hashPassword(password, salt);

            String fields = EMAIL_FIELD + ", " + PASSWORD_FIELD + ", " + SALT_FIELD;
            String values = "'" + email + "', '" + hashedPassword + "', '" + salt + "'";

            insert(USERS_TABLE, fields, values);

            System.out.println("User created: ");
            System.out.println("Email: " + email);
            System.out.println("Hashed password: " + hashedPassword);
            System.out.println("Salt: " + salt);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Error hashing password: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error inserting user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean verifyAccount(String email) { // Verifies that account exists with email
        System.out.println("verifyAccount ran with user: " + email);

        if (email == null || email.isEmpty()) {
            System.out.println("Email is null or empty!");
            return false;
        }

        return isKeyAvailable(USERS_TABLE, EMAIL_FIELD, "'" + email + "'");
    }

    public boolean verifyPassword(String email, String providedPassword) { // Verifies if provided password matches stored hash
        try {
            String storedHash = getValue(USERS_TABLE, EMAIL_FIELD, "'" + email + "'", PASSWORD_FIELD);
            String salt = getValue(USERS_TABLE, EMAIL_FIELD, "'" + email + "'", SALT_FIELD);

            if (storedHash == null || salt == null) {
                System.out.println("Account with email " + email + " not found.");
                return false;
            }

            return PasswordHasher.verifyPassword(providedPassword, storedHash, salt);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Error verifying password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
