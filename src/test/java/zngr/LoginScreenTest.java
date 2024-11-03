package zngr;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import static org.junit.jupiter.api.Assertions.*;

public class LoginScreenTest extends ApplicationTest {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Text messageLabel;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/src/main/resources/zngr/primary.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        usernameField = (TextField) scene.lookup("#usernameField");
        passwordField = (PasswordField) scene.lookup("#passwordField");
        loginButton = (Button) scene.lookup("#loginButton");
        messageLabel = (Text) scene.lookup("#messageLabel");
    }

    @Test
    public void testSuccessfulLogin() {
        // Given correct username and password
        Account account = new Account();
        account.addAccount("testUser", "testPassword");

        // When entering correct credentials
        clickOn(usernameField).write("testUser");
        clickOn(passwordField).write("testPassword");
        clickOn(loginButton);

        // Then message label should indicate successful login
        assertEquals("Login successful", messageLabel.getText());
    }

    @Test
    public void testFailedLogin() {
        // Incorrect password
        Account account = new Account();
        account.addAccount("testUser", "testPassword");

        // When entering the incorrect password
        clickOn(usernameField).write("testUser");
        clickOn(passwordField).write("wrongPassword");
        clickOn(loginButton);

        // Then the message label should indicate login failure
        assertEquals("Incorrect password. Attempts left: 2", messageLabel.getText());
    }
}
