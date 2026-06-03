package borrow;

public class BorrowAutomaton {
    private BorrowState currentState = BorrowState.Q0_IDLE;

    public BorrowState getCurrentState() {
        return currentState;
    }

    public void transition(BorrowInput input) {
        BorrowState oldState = currentState;

        switch (currentState) {
            case Q0_IDLE:
                if (input == BorrowInput.INPUT_SEARCH) currentState = BorrowState.Q1_SEARCHING;
                break;

            case Q1_SEARCHING:
                if (input == BorrowInput.BOOK_FOUND) currentState = BorrowState.Q3_CHECK_AVAILABILITY;
                else if (input == BorrowInput.BOOK_NOT_FOUND) currentState = BorrowState.Q2_BOOK_NOT_FOUND;
                break;

            case Q3_CHECK_AVAILABILITY:
                if (input == BorrowInput.CONFIRM_BORROW) currentState = BorrowState.Q4_BORROWED;
                else if (input == BorrowInput.BOOK_IS_LOST) currentState = BorrowState.Q7_BOOK_LOST;
                else if (input == BorrowInput.BORROW_REJECTED) currentState = BorrowState.Q5_REJECTED;
                else if (input == BorrowInput.GO_BACK_TO_IDLE) currentState = BorrowState.Q0_IDLE;
                break;

            case Q5_REJECTED:
                /*
                 * RESERVATION TRANSITION:
                 * From the rejected state, the user may choose to reserve the book.
                 * REQUEST_RESERVATION drives to Q6_RESERVED (accept).
                 * GO_BACK_TO_IDLE resets to start for a new search.
                 */
                if (input == BorrowInput.REQUEST_RESERVATION) currentState = BorrowState.Q6_RESERVED;
                else if (input == BorrowInput.GO_BACK_TO_IDLE) currentState = BorrowState.Q0_IDLE;
                break;

            case Q2_BOOK_NOT_FOUND:
            case Q4_BORROWED:
            case Q6_RESERVED:
            case Q7_BOOK_LOST:
                if (input == BorrowInput.GO_BACK_TO_IDLE) currentState = BorrowState.Q0_IDLE;
                break;
        }

        if (oldState != currentState) {
            System.out.println("\n>>> [TRANSITION]: " + oldState.name() + " --(" + input.name() + ")--> " + currentState.name());
        }
    }
}