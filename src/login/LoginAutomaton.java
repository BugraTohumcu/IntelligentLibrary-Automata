package login;

public class LoginAutomaton {
    private LoginState currentState = LoginState.Q0_ID_WAITING;

    public LoginState getCurrentState() { 
        return currentState; 
    }

    public void transition(LoginInput input) {
        LoginState oldState = currentState; 

        switch (currentState) {
            case Q0_ID_WAITING:
                if (input == LoginInput.ENTER_ID) {
                    currentState = LoginState.Q1_ID_VALIDATION;
                }
                break;

            case Q1_ID_VALIDATION:
                if (input == LoginInput.ID_VALID) {
                    currentState = LoginState.Q2_PASSWORD_WAITING;
                } else if (input == LoginInput.ID_INVALID) {
                    currentState = LoginState.Q0_ID_WAITING;
                }
                break;

            case Q2_PASSWORD_WAITING:
                if (input == LoginInput.ENTER_PASSWORD) {
                    currentState = LoginState.Q3_PASSWORD_VALIDATION;
                }
                break;

            case Q3_PASSWORD_VALIDATION:
                if (input == LoginInput.PASSWORD_VALID) {
                    currentState = LoginState.Q4_LOGIN_SUCCESS;
                } else if (input == LoginInput.PASSWORD_INVALID) {
                    currentState = LoginState.Q2_PASSWORD_WAITING;
                }
                break;

            case Q4_LOGIN_SUCCESS:
                // Accept state, no outgoing transitions
                break;
        }

        if (oldState != currentState) {
            System.out.println("\n>>> [TRANSITION]: " + oldState.name() + " --(" + input.name() + ")--> " + currentState.name());
        } else {
            System.out.println("\n>>> [INVALID TRANSITION/SELF-LOOP]: State remained " + currentState.name() + " due to input " + input.name());
        }
    }
}