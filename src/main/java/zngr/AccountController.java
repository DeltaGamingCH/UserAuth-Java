package zngr;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class AccountController {

    private Account account;
    
    @FXML
    private Button btLogin;
    
    @FXML
    private Button btLogout;

    @FXML
    private Button btSignUp;

    @FXML
    private Label lbLoginMessage;

    @FXML
    private Label lbSignUpMessage;

    @FXML
    private PasswordField pfLoginPassword;

    @FXML
    private PasswordField pfSignUpConfirmPassword;

    @FXML
    private PasswordField pfSignUpPassword;
     
    @FXML
    private TabPane tabPane;

    @FXML
    private TextField tfSignUpEmail;

    @FXML
    private TextField tfUsername;

    // Password Reset
    @FXML
    private GridPane passwordResetContainer;

    @FXML
    private Tab passwordResetTab;

    @FXML
    private GridPane loginContainer;

    @FXML
    private Button btResetPassword;

    @FXML
    private PasswordField pfNewPassword;

    @FXML
    private PasswordField pfConfirmNewPassword;

    @FXML
    private Button btConfirmResetPassword;

    @FXML
    private TextField tfResetEmail;

    @FXML
    private Label lbResetPasswordMessage;

    @FXML
    private Button btBackToLogin;

    private LoginBlocker loginBlocker;

    @FXML
    private void initialize() throws Exception {
        // create and init DB-Tables
        account = new Account();
        account.initAccount();
        loginBlocker = new LoginBlocker(account, tfUsername, pfLoginPassword, btLogin, lbLoginMessage);
        System.out.println("Account table initialized.");
    }   

    @FXML
    private void onSignUp(ActionEvent event) throws Exception  {
        // verify name, password and confirmed password
        String name = tfSignUpEmail.getText();
        if (name.isEmpty()) {
            lbSignUpMessage.setText("Type in email");
            return;
        }

        String pw = pfSignUpPassword.getText().trim();
        if (pw.equals("")) {
            lbSignUpMessage.setText("Enter a plausible password");
            return;
        }

        if (!pw.equals(pfSignUpConfirmPassword.getText())) {
            lbSignUpMessage.setText("Password and confirmed password are not identical");
            return;
        }

        // verify account 
        if (account.verifyAccount(name)) {
            lbSignUpMessage.setText("Email " + name + " has already an account");
            return;
        }

        account.addAccount(name, pw);
        
        // select tab 'Log In'
        tabPane.getTabs().get(0).setDisable(true);
        
        // reset login and signup
        resetLogin();
        resetSignup();
        
        // select tab 'Log in'
        tabPane.getSelectionModel().select(1);
    }

    @FXML
    private void onLogin(ActionEvent event) {
        resetPassword(null); // Hide password reset fields

        String email = tfUsername.getText().trim();
        String password = pfLoginPassword.getText().trim();

        // Enable LoginBlocker to manage login attempts
        if (loginBlocker.handleLoginAttempts(email, password)) {
            tabPane.getTabs().get(0).setDisable(true);
            tabPane.getTabs().get(1).setDisable(true);
            tabPane.getTabs().get(2).setDisable(true);
            tabPane.getTabs().get(3).setDisable(false);
            tabPane.getSelectionModel().select(3);
        } else {
            lbLoginMessage.setText("Incorrect email or password.");
            tabPane.getTabs().get(0).setDisable(false);
        }
    }


    @FXML
    private void onLogout(ActionEvent event) {
        // set tabs
        tabPane.getTabs().get(0).setDisable(false);
        tabPane.getTabs().get(1).setDisable(false);
        tabPane.getTabs().get(2).setDisable(true);
        tabPane.getTabs().get(3).setDisable(true);

        resetLogin();   
        tabPane.getSelectionModel().select(1);
    }

    @FXML
    private void onResetPasswordClick(ActionEvent event) { // Login Tab: ResetButton
        passwordResetTab.setDisable(false);
        tabPane.getSelectionModel().select(2);
    }

    @FXML
    private void onBackToLoginClick(ActionEvent event) { // ResetPassword: Select Login page
        passwordResetTab.setDisable(true);
        tabPane.getSelectionModel().select(1);
    }

    @FXML
    private void resetPassword(ActionEvent event) { // Hide password reset fields
        passwordResetTab.setDisable(true);
    }

    @FXML
    private void onConfirmResetPassword(ActionEvent event) throws Exception {
        String email = tfResetEmail.getText().trim();
        String newPassword = pfNewPassword.getText().trim();
        String confirmPassword = pfConfirmNewPassword.getText().trim();

        if (email.isEmpty()) {
            lbResetPasswordMessage.setText("Please enter an email");
            return;
        }

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            lbResetPasswordMessage.setText("Please enter and confirm the new password");
            return;
        }

        PasswordReset passwordReset = new PasswordReset(account);

        String resultMessage = passwordReset.resetPassword(email, newPassword, confirmPassword);

        lbResetPasswordMessage.setText(resultMessage);

        if (resultMessage.equals("Password has been reset successfully")) {
            resetPasswordFields();
            onBackToLoginClick(null);
        }
    }

    private void resetPasswordFields() {
        tfResetEmail.setText("");
        pfNewPassword.setText("");
        pfConfirmNewPassword.setText("");
        lbResetPasswordMessage.setText("");
    }

    private void resetLogin() {
        tfUsername.setText("");
        pfLoginPassword.setText("");
        lbLoginMessage.setText("Login with your account");
    } 

    private void resetSignup() {
        tfSignUpEmail.setText("");
        pfSignUpPassword.setText("");
        pfSignUpConfirmPassword.setText("");
        lbSignUpMessage.setText("Create Account");
    }
}
