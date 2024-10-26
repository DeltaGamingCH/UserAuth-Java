package zngr;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Timer;
import java.util.TimerTask;

public class LoginBlocker {
    private final Account account;
    private final int[] blockTimes = {10, 30, 60, 180, 300};
    private Timer blockTimer;
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Button loginButton;
    private final Label messageLabel;

    public LoginBlocker(Account account, TextField usernameField, PasswordField passwordField, Button loginButton, Label messageLabel) { //Constructor
        this.account = account;
        this.usernameField = usernameField;
        this.passwordField = passwordField;
        this.loginButton = loginButton;
        this.messageLabel = messageLabel;
    }

    public boolean handleLoginAttempts(String email, String password) { // Checks and handles the amount of failed login attempts
        if (!account.verifyAccount(email)) {
            messageLabel.setText("Account does not exist.");
            return false;
        }

        boolean loginSuccess = account.verifyPassword(email, password);
        int attempts = account.getLoginAttempts(email);

        if (loginSuccess) {
            account.resetLoginAttempts(email);
            return true;
        } else if (attempts >= 3) {
            blockLogin(email);
            return false;
        } else {
            messageLabel.setText("Incorrect password. Attempts left: " + (3 - attempts));
            return false;
        }
    }

    private void blockLogin(String email) { // Blocks the login temporarily
        int attempts = account.getLoginAttempts(email);
        int blockIndex = Math.min(attempts - 3, blockTimes.length - 1);
        int blockTime = blockTimes[blockIndex];

        disableLoginControls(true);
        startBlockTimer(blockTime);
    }

    private void startBlockTimer(int seconds) { // Starts the block timer
        if (blockTimer != null) {
            blockTimer.cancel();
        }
        blockTimer = new Timer();
        blockTimer.scheduleAtFixedRate(new TimerTask() {
            int remainingSeconds = seconds;

            @Override
            public void run() {
                if (remainingSeconds <= 0) {
                    Platform.runLater(() -> {
                        disableLoginControls(false);
                        messageLabel.setText("Enter a valid email and password.");
                    });
                    blockTimer.cancel();
                } else {
                    Platform.runLater(() -> messageLabel.setText("Please wait " + remainingSeconds + " seconds before trying again."));
                    remainingSeconds--;
                }
            }
        }, 0, 1000);
    }
    private void disableLoginControls(boolean disable) { // Disables the login elements
        usernameField.setDisable(disable);
        passwordField.setDisable(disable);
        loginButton.setDisable(disable);
    }
}
