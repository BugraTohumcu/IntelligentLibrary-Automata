package return_renew;


/**
 * Represents the input alphabet (Sigma) for the Return and Renewal Automaton.
 * These are the actions or system events that trigger state transitions.
 */
public enum ReturnRenewInput {
    BORROW_FOUND,       // User has active borrowed books
    BORROW_NOT_FOUND,   // User has no books (triggers exit)
    OVERDUE,            // The selected book is past its due date
    NOT_OVERDUE,        // The selected book is within the valid time frame
    RETURN_BOOK,        // User decides to return the book
    NOT_RETURN,         // User refuses/fails to return an overdue book
    REQUEST_RENEWAL,    // User asks for extra time to keep the book
    RENEWAL_ACCEPTED,   // System approves the renewal request
    RENEWAL_DENIED,     // System rejects the renewal (e.g., max limit reached)
    EXIT_OR_RETURN,     // User completes action or chooses to leave
    RETURN_MENU         // Returns to the main menu
}