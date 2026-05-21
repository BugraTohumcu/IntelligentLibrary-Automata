package login;

public enum LoginState {
    Q0_ID_WAITING(true, false, "System is waiting for user ID input..."),
    Q1_ID_VALIDATION(false, false, "Validating entered ID against records..."),
    Q2_PASSWORD_WAITING(false, false, "ID accepted. Waiting for user password..."),
    Q3_PASSWORD_VALIDATION(false, false, "Validating password credentials..."),
    Q4_LOGIN_SUCCESS(false, true, "Login successful! Proceeding to Main Menu.");

    private final boolean isInitial;
    private final boolean isAccept;
    private final String consoleMessage;

    LoginState(boolean isInitial, boolean isAccept, String consoleMessage) {
        this.isInitial = isInitial;
        this.isAccept = isAccept;
        this.consoleMessage = consoleMessage;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public String getConsoleMessage() {
        return consoleMessage;
    }
}