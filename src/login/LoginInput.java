package login;

public enum LoginInput {
    ENTER_ID("User initiated ID input event."),
    ID_VALID("The entered ID exists in the system database."),
    ID_INVALID("The entered ID does not exist or is malformed."),
    ENTER_PASSWORD("User initiated password input event."),
    PASSWORD_VALID("Password matches the corresponding ID credentials."),
    PASSWORD_INVALID("Incorrect password provided.");

    private final String description;

    LoginInput(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}