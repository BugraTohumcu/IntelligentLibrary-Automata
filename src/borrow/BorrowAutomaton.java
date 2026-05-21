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
                else if (input == BorrowInput.BORROW_REJECTED) currentState = BorrowState.Q5_REJECTED;
                else if (input == BorrowInput.GO_BACK_TO_IDLE) currentState = BorrowState.Q0_IDLE;
                break;
                
            case Q2_BOOK_NOT_FOUND:
            case Q4_BORROWED:
            case Q5_REJECTED:
                if (input == BorrowInput.GO_BACK_TO_IDLE) currentState = BorrowState.Q0_IDLE;
                break;
        }

        /* * ACADEMIC REQUIREMENT VERIFICATION:
         * Triggers explicit state transition log to fulfill the formal method representation.
         * Shows teammate and evaluator the step-by-step trace of the DFA execution path.
         */
        if (oldState != currentState) {
            System.out.println("\n>>> [TRANSITION]: " + oldState.name() + " --(" + input.name() + ")--> " + currentState.name());
        }
    }
}