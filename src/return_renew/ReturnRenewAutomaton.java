package return_renew;

/**
 * Formal DFA execution engine managing the tracking mechanics for book returns or renewal processes.
 * Evaluates operational inputs (Sigma symbols) to alter the configuration state of the system.
 */
public class ReturnRenewAutomaton {
    
    private ReturnRenewState currentState;

    public ReturnRenewAutomaton() {
        // Initializes the system structure at the start state (q0)
        this.currentState = ReturnRenewState.Q0_MAIN_PAGE;
    }

    /**
     * Gets the active operational configuration state of the finite state tracker.
     * @return The current ReturnRenewState enum token.
     */
    public ReturnRenewState getCurrentState() {
        return currentState;
    }

    /**
     * Formal transition function (delta: Q x Sigma -> Q).
     * Modifies system state based on operational input tokens. Unhandled inputs trigger self-loops.
     * @param input Formal event token driving the structural state modifications.
     */
    public void transition(ReturnRenewInput input) {
        ReturnRenewState oldState = currentState;
        
        switch (currentState) {
            case Q0_MAIN_PAGE:
                if (input == ReturnRenewInput.BORROW_FOUND) {
                    currentState = ReturnRenewState.Q2_CHECK_OVERDUE;
                } else if (input == ReturnRenewInput.BORROW_NOT_FOUND) {
                    currentState = ReturnRenewState.Q1_EXIT_SYSTEM;
                }
                break;

            case Q2_CHECK_OVERDUE:
                if (input == ReturnRenewInput.OVERDUE) {
                    currentState = ReturnRenewState.Q3_LATE_PENALTY_DECISION;
                } else if (input == ReturnRenewInput.NOT_OVERDUE) {
                    currentState = ReturnRenewState.Q4_RENEWAL_RETURN_MENU;
                } else if (input == ReturnRenewInput.RETURN_MENU) { 
                    currentState = ReturnRenewState.Q1_EXIT_SYSTEM;
                }
                break;

            case Q3_LATE_PENALTY_DECISION:
                if (input == ReturnRenewInput.RETURN_BOOK) {
                    currentState = ReturnRenewState.Q1_EXIT_SYSTEM;
                } else if (input == ReturnRenewInput.NOT_RETURN) {
                    currentState = ReturnRenewState.Q7_SUSPEND;
                }
                break;

            case Q4_RENEWAL_RETURN_MENU:
                if (input == ReturnRenewInput.REQUEST_RENEWAL) {
                    currentState = ReturnRenewState.Q5_RENEWAL_APPROVAL;
                } else if (input == ReturnRenewInput.EXIT_OR_RETURN) {
                    currentState = ReturnRenewState.Q1_EXIT_SYSTEM;
                }
                break;

            case Q5_RENEWAL_APPROVAL:
                if (input == ReturnRenewInput.RENEWAL_ACCEPTED) {
                    currentState = ReturnRenewState.Q4_RENEWAL_RETURN_MENU;
                } else if (input == ReturnRenewInput.RENEWAL_DENIED) {
                    currentState = ReturnRenewState.Q6_REJECTED_STATE;
                }
                break;

            case Q6_REJECTED_STATE:
                if (input == ReturnRenewInput.EXIT_OR_RETURN) {
                    currentState = ReturnRenewState.Q1_EXIT_SYSTEM;
                }
                break;

            case Q1_EXIT_SYSTEM:
            case Q7_SUSPEND:
                // Accept configurations block further state mutation pathways.
                break;

            default:
                break;
        }

        /* * FORMAL METHOD COMPLIANCE:
         * Intercepts successful state modifications and pipes an unmasked transition diagram trace.
         * Satisfies project guidelines requiring live visibility into current and next states.
         */
        if (oldState != currentState) {
            System.out.println("\n>>> [TRANSITION]: " + oldState.name() + " --(" + input.name() + ")--> " + currentState.name());
        }
    }
}