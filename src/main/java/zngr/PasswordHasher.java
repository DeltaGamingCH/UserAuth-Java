package zngr;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Properties;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHasher {
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_LENGTH = 16;

    private static String loadPepper() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
            return properties.getProperty("pepper");
        } catch (IOException e) {
            throw new RuntimeException("Error loading pepper from config file", e);
        }
    }

    public static String generateSalt() { // Generates a new salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException { // Hashes password with salt and pepper
        String pepper = loadPepper();
        String pepperedPassword = password + pepper;
        PBEKeySpec spec = new PBEKeySpec(pepperedPassword.toCharArray(), Base64.getDecoder().decode(salt), ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String providedPassword, String storedHash, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException { // Verifies hashed password
        String hashedProvidedPassword = hashPassword(providedPassword, salt);
        return hashedProvidedPassword.equals(storedHash);
    }
}
