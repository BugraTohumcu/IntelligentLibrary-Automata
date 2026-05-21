package return_renew;


/**
 * Represents the finite states (Q) of the Return and Renewal System.
 * Q1 and Q7 act as our Accept (Final) States.
 */
public enum ReturnRenewState {
    Q0_MAIN_PAGE(false, "Checking user's current book inventory..."),
    Q1_EXIT_SYSTEM(true, "Transaction complete. Exiting module..."), // Accept State
    Q2_CHECK_OVERDUE(false, "Analyzing book due dates..."),
    Q3_LATE_PENALTY_DECISION(false, "Book is overdue! Awaiting user action..."),
    Q4_RENEWAL_RETURN_MENU(false, "Book is valid. Select: Return or Renew?"),
    Q5_RENEWAL_APPROVAL(false, "Processing renewal request..."),
    Q6_REJECTED_STATE(false, "Renewal denied! Please return the book."),
    Q7_SUSPEND(true, "Penalty unpaid/Book not returned. Account suspended!"); // Accept State

    private final boolean isAcceptState;
    private final String message;

    ReturnRenewState(boolean isAcceptState, String description) {
        this.isAcceptState = isAcceptState;
        this.message = description;
    }

    public boolean isAcceptState() {
        return isAcceptState;
    }

    public String getConsoleMessage() {
        return message;
    }
}