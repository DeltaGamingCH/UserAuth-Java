package zngr;

public class Account extends DatabaseAPI {

    private static final String USERS_TABLE = "users";
    private static final String EMAIL_FIELD = "email";
    private static final String PASSWORD_FIELD = "password";

    public void initAccount() {
        String fields = "userid INTEGER PRIMARY KEY AUTOINCREMENT, " + "email TEXT UNIQUE, " + "password TEXT";
        createTable(USERS_TABLE, fields);
    }

    public void addAccount(String email, String password) {
        String fields = EMAIL_FIELD + ", " + PASSWORD_FIELD;
        String values = "'" + email + "', '" + password + "'";

        insert(USERS_TABLE, fields, values);

        try {
            insert(USERS_TABLE, fields, values);
            System.out.println("User created: ");
            System.out.println("Email: " + email);
            System.out.println("Password: " + password);
        } catch (Exception e) {
            System.out.println("Error inserting user: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public boolean verifyAccount(String userName) {
        System.out.println("verifyAccount ran with user: " + userName);

        if (userName == null || userName.isEmpty()) {
            System.out.println("User name is null or empty!");
            return false;
        }

        return isKeyAvailable(USERS_TABLE, EMAIL_FIELD, "'" + userName + "'");
    }

    public boolean verifyPassword(String userName, String password) {
        System.out.println("verifyPassword not implemented");
        return false;
    }
}

