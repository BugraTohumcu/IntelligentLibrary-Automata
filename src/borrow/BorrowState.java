package borrow;

public enum BorrowState {
    Q0_IDLE("Waiting for user to trigger search..."),
    Q1_SEARCHING("Processing the book query logic..."),
    Q2_BOOK_NOT_FOUND("The book you searched for does not exist in our library."),
    Q3_CHECK_AVAILABILITY("Books found! Checking status details..."),
    Q4_BORROWED("Success! Book borrowed successfully."),          // Accept State
    Q5_REJECTED("Action denied! The book is already taken by someone else."),
    Q6_RESERVED("Reservation confirmed! You will be notified when the book is available."), // Accept State
    Q7_BOOK_LOST("This book is marked as lost and cannot be borrowed."); // Accept State

    private final String message;
    BorrowState(String message) { this.message = message; }
    public String getConsoleMessage() { return message; }
}