package zngr;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class LoginBlockerTest extends ApplicationTest {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label messageLabel;
    private LoginBlocker loginBlocker;

    @BeforeEach
    public void setUp() {
        usernameField = new TextField();
        passwordField = new PasswordField();
        loginButton = new Button();
        messageLabel = new Label();

        // Create mock or real Account instance
        Account mockAccount = new Account();
        mockAccount.initAccount();
        mockAccount.addAccount("test@example.com", "correctPassword");

        // Initialize LoginBlocker with account and UI components
        loginBlocker = new LoginBlocker(mockAccount, usernameField, passwordField, loginButton, messageLabel);
    }

    @Test
    public void testPasswordLockAfterThreeFailedAttempts() {
        String email = "test@example.com";
        String wrongPassword = "wrongPassword";

        // First failed attempt
        boolean result = loginBlocker.handleLoginAttempts(email, wrongPassword);
        assertFalse(result);
        assertEquals("Incorrect password. Attempts left: 2", messageLabel.getText());

        // Second failed attempt
        result = loginBlocker.handleLoginAttempts(email, wrongPassword);
        assertFalse(result);
        assertEquals("Incorrect password. Attempts left: 1", messageLabel.getText());

        // Third failed attempt
        result = loginBlocker.handleLoginAttempts(email, wrongPassword);
        assertFalse(result);
        assertTrue(loginButton.isDisabled());
        assertTrue(usernameField.isDisabled());
        assertTrue(passwordField.isDisabled());

        // Assert correct lock message displayed
        assertEquals("Please wait 10 seconds before trying again.", messageLabel.getText());
    }
}
