package zngr;

public class PasswordReset {
    private Account account;

    public PasswordReset(Account account) {
        this.account = account;
    }

    public String resetPassword(String email, String newPassword, String confirmPassword) throws Exception {
        if (!account.verifyAccount(email)) {
            return "Account with this email does not exist";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "Passwords do not match";
        }

        String newSalt = PasswordHasher.generateSalt();
        account.updateAccount(email, newPassword, newSalt);

        return "Password has been reset successfully";
    }
}
