package zngr;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Account extends DatabaseAPI {

    private static final String USERS_TABLE = "users";
    private static final String SALT_TABLE = "salts";
    private static final String EMAIL_FIELD = "email";
    private static final String PASSWORD_FIELD = "password";
    private static final String SALT_FIELD = "salt";
    private static final String USER_ID_FIELD = "userid";
    private static final String LOGIN_ATTEMPTS_FIELD = "failed_login_attempts";

    public void initAccount() { // Create users table, email, hashed password, salt
        String fields = "userid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "failed_login_attempts INTEGER DEFAULT 0";
        createTable(USERS_TABLE, fields);

        String saltFields = "userid INTEGER PRIMARY KEY, " +
                "salt TEXT, " +
                "FOREIGN KEY (userid) REFERENCES " + USERS_TABLE + "(userid)";
        createTable(SALT_TABLE, saltFields);
    }

    public void addAccount(String email, String password) { // Create new user with email, hashed password, salt
        try {
            String salt = PasswordHasher.generateSalt();
            String hashedPassword = PasswordHasher.hashPassword(password, salt);

            String userFields = EMAIL_FIELD + ", " + PASSWORD_FIELD + ", " + LOGIN_ATTEMPTS_FIELD;
            String userValues = "'" + email + "', '" + hashedPassword + "', 0";
            int userId = insert(USERS_TABLE, userFields, userValues);

            if (userId != -1) {
                String saltFields = USER_ID_FIELD + ", " + SALT_FIELD;
                String saltValues = userId + ", '" + salt + "'";
                insert(SALT_TABLE, saltFields, saltValues);

                System.out.println("User created: ");
                System.out.println("Email: " + email);
                System.out.println("Hashed password: " + hashedPassword);
                System.out.println("Salt: " + salt);
            } else {
                System.out.println("Failed to create user. No user ID returned.");
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Error hashing password: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error inserting user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateAccount(String email, String newPassword, String newSalt) { // Updated account information
        try {
            String hashedPassword = PasswordHasher.hashPassword(newPassword, newSalt);
            String userId = getValue(USERS_TABLE, EMAIL_FIELD, "'" + email + "'", USER_ID_FIELD);

            update(USERS_TABLE, PASSWORD_FIELD, "'" + hashedPassword + "'", EMAIL_FIELD, "'" + email + "'");
            update(SALT_TABLE, SALT_FIELD, "'" + newSalt + "'", USER_ID_FIELD, userId);

            System.out.println("Password and salt updated for user: " + email);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Error hashing password: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error updating password: " + e.getMessage());
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
            String userId = getValue(USERS_TABLE, EMAIL_FIELD, "'" + email + "'", USER_ID_FIELD);
            String storedHash = getValue(USERS_TABLE, EMAIL_FIELD, "'" + email + "'", PASSWORD_FIELD);
            String salt = getValue(SALT_TABLE, USER_ID_FIELD, userId, SALT_FIELD);

            if (storedHash == null || salt == null) {
                System.out.println("Account with email " + email + " not found.");
                return false;
            }

            System.out.println("Stored hash: " + storedHash);
            System.out.println("Salt: " + salt);

            boolean isCorrect = PasswordHasher.verifyPassword(providedPassword, storedHash, salt);

            if (isCorrect) {
                resetLoginAttempts(email);
                System.out.println("Password verification successful for user: " + email);
            } else {
                incrementLoginAttempts(email);
                System.out.println("Password verification failed for user: " + email);
            }

            return isCorrect;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Error verifying password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void incrementLoginAttempts(String email) { // Increments the amount of failed login attempts
        try {
            int currentAttempts = Integer.parseInt(getValue(USERS_TABLE, EMAIL_FIELD, "'" + email + "'", LOGIN_ATTEMPTS_FIELD));
            update(USERS_TABLE, LOGIN_ATTEMPTS_FIELD, String.valueOf(currentAttempts + 1), EMAIL_FIELD, "'" + email + "'");
        } catch (Exception e) {
            System.out.println("Error incrementing login attempts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void resetLoginAttempts(String email) { // Resets the amount of login attempts
        try {
            update(USERS_TABLE, LOGIN_ATTEMPTS_FIELD, "0", EMAIL_FIELD, "'" + email + "'");
        } catch (Exception e) {
            System.out.println("Error resetting login attempts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getLoginAttempts(String email) { // Gets the amounts of failed login attempts
        try {
            return Integer.parseInt(getValue(USERS_TABLE, EMAIL_FIELD, "'" + email + "'", LOGIN_ATTEMPTS_FIELD));
        } catch (Exception e) {
            System.out.println("Error getting login attempts: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}
